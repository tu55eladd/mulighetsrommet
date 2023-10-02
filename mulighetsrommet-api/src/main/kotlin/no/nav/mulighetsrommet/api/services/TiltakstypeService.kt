package no.nav.mulighetsrommet.api.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.mulighetsrommet.api.domain.dto.TiltakstypeNokkeltallDto
import no.nav.mulighetsrommet.api.repositories.AvtaleRepository
import no.nav.mulighetsrommet.api.repositories.DeltakerRepository
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.repositories.TiltakstypeRepository
import no.nav.mulighetsrommet.api.routes.v1.responses.PaginatedResponse
import no.nav.mulighetsrommet.api.routes.v1.responses.Pagination
import no.nav.mulighetsrommet.api.utils.PaginationParams
import no.nav.mulighetsrommet.api.utils.TiltakstypeFilter
import no.nav.mulighetsrommet.domain.dto.TiltakstypeDto
import no.nav.mulighetsrommet.utils.CacheUtils
import java.util.*
import java.util.concurrent.TimeUnit

class TiltakstypeService(
    private val tiltakstypeRepository: TiltakstypeRepository,
    private val tiltaksgjennomforingRepository: TiltaksgjennomforingRepository,
    private val avtaleRepository: AvtaleRepository,
    private val deltakerRepository: DeltakerRepository,
) {

    private val cacheBySanityId: Cache<UUID, TiltakstypeDto> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(500)
        .recordStats()
        .build()

    fun getWithFilter(
        tiltakstypeFilter: TiltakstypeFilter,
        paginationParams: PaginationParams,
    ): PaginatedResponse<TiltakstypeDto> {
        val (totalCount, items) = tiltakstypeRepository.getAllSkalMigreres(
            tiltakstypeFilter,
            paginationParams,
        )

        return PaginatedResponse(
            data = items,
            pagination = Pagination(
                totalCount = totalCount,
                currentPage = paginationParams.page,
                pageSize = paginationParams.limit,
            ),
        )
    }

    fun getById(id: UUID): TiltakstypeDto? {
        return tiltakstypeRepository.get(id)
    }

    fun getBySanityId(sanityId: UUID): TiltakstypeDto? {
        return CacheUtils.tryCacheFirstNullable(cacheBySanityId, sanityId) {
            tiltakstypeRepository.getBySanityId(sanityId)
        }
    }

    fun getNokkeltallForTiltakstype(tiltakstypeId: UUID): TiltakstypeNokkeltallDto {
        val antallGjennomforinger =
            tiltaksgjennomforingRepository.countGjennomforingerForTiltakstypeWithId(tiltakstypeId)
        val antallAvtaler = avtaleRepository.countAktiveAvtalerForTiltakstypeWithId(tiltakstypeId)
        val antallDeltakere = deltakerRepository.countAntallDeltakereForTiltakstypeWithId(tiltakstypeId)
        return TiltakstypeNokkeltallDto(
            antallTiltaksgjennomforinger = antallGjennomforinger,
            antallAvtaler = antallAvtaler,
            antallDeltakere = antallDeltakere,
        )
    }
}
