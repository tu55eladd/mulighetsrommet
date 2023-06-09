package no.nav.mulighetsrommet.api.services

import arrow.core.Either
import arrow.core.flatMap
import no.nav.mulighetsrommet.api.domain.dto.TiltaksgjennomforingNokkeltallDto
import no.nav.mulighetsrommet.api.repositories.DeltakerRepository
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.routes.v1.responses.PaginatedResponse
import no.nav.mulighetsrommet.api.routes.v1.responses.Pagination
import no.nav.mulighetsrommet.api.utils.AdminTiltaksgjennomforingFilter
import no.nav.mulighetsrommet.api.utils.PaginationParams
import no.nav.mulighetsrommet.database.utils.DatabaseOperationError
import no.nav.mulighetsrommet.database.utils.QueryResult
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingDbo
import no.nav.mulighetsrommet.domain.dto.TiltaksgjennomforingAdminDto
import no.nav.mulighetsrommet.domain.dto.TiltaksgjennomforingNotificationDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class TiltaksgjennomforingService(
    private val tiltaksgjennomforingRepository: TiltaksgjennomforingRepository,
    private val deltakerRepository: DeltakerRepository,
    private val sanityTiltaksgjennomforingService: SanityTiltaksgjennomforingService,
    private val virksomhetService: VirksomhetService,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    suspend fun upsert(dbo: TiltaksgjennomforingDbo): QueryResult<TiltaksgjennomforingAdminDto> {
        virksomhetService.hentEnhet(dbo.virksomhetsnummer)
        return tiltaksgjennomforingRepository.upsert(dbo)
            .flatMap { tiltaksgjennomforingRepository.get(dbo.id) }
            .onLeft { log.error("Klarte ikke lagre tiltaksgjennomføring", it.error) }
            .map { it!! } // If upsert is successful it should exist here
            .onRight {
                sanityTiltaksgjennomforingService.opprettSanityTiltaksgjennomforing(it)
            }
    }

    fun get(id: UUID): QueryResult<TiltaksgjennomforingAdminDto?> =
        tiltaksgjennomforingRepository.get(id)
            .onLeft { log.error("Klarte ikke hente tiltaksgjennomføring", it.error) }

    fun getAll(
        pagination: PaginationParams,
        filter: AdminTiltaksgjennomforingFilter,
    ): Either<DatabaseOperationError, PaginatedResponse<TiltaksgjennomforingAdminDto>> =
        tiltaksgjennomforingRepository
            .getAll(pagination, filter)
            .onLeft { log.error("Klarte ikke hente tiltaksgjennomføringer", it.error) }
            .map { (totalCount, data) ->
                PaginatedResponse(
                    pagination = Pagination(
                        totalCount = totalCount,
                        currentPage = pagination.page,
                        pageSize = pagination.limit,
                    ),
                    data = data,
                )
            }

    fun getNokkeltallForTiltaksgjennomforing(tiltaksgjennomforingId: UUID): TiltaksgjennomforingNokkeltallDto =
        TiltaksgjennomforingNokkeltallDto(
            antallDeltakere = deltakerRepository.countAntallDeltakereForTiltakstypeWithId(tiltaksgjennomforingId),
        )

    fun getAllGjennomforingerSomNarmerSegSluttdato(): List<TiltaksgjennomforingNotificationDto> {
        return tiltaksgjennomforingRepository.getAllGjennomforingerSomNarmerSegSluttdato()
    }

    fun kobleGjennomforingTilAvtale(gjennomforingId: UUID, avtaleId: UUID? = null) {
        return tiltaksgjennomforingRepository.updateAvtaleIdForGjennomforing(gjennomforingId, avtaleId)
    }

    fun getBySanitIds(sanityIds: List<UUID>): Map<String, TiltaksgjennomforingAdminDto> {
        return tiltaksgjennomforingRepository.getBySanityIds(sanityIds)
    }

    fun getAllMidlertidigStengteGjennomforingerSomNarmerSegSluttdato(): List<TiltaksgjennomforingNotificationDto> {
        return tiltaksgjennomforingRepository.getAllMidlertidigStengteGjennomforingerSomNarmerSegSluttdato()
    }
}
