package no.nav.mulighetsrommet.api.services

import arrow.core.Either
import arrow.core.flatMap
import no.nav.mulighetsrommet.api.domain.dbo.UtkastDbo
import no.nav.mulighetsrommet.api.domain.dto.UtkastDto
import no.nav.mulighetsrommet.api.repositories.UtkastRepository
import no.nav.mulighetsrommet.api.routes.v1.responses.NotFound
import no.nav.mulighetsrommet.api.routes.v1.responses.ServerError
import no.nav.mulighetsrommet.api.routes.v1.responses.StatusResponse
import no.nav.mulighetsrommet.api.utils.UtkastFilter
import java.util.*

class UtkastService(
    private val utkastRepository: UtkastRepository,
) {
    fun get(id: UUID): StatusResponse<UtkastDto> {
        return utkastRepository.get(id)
            .mapLeft { ServerError("Feil ved henting av utkast med id: $id") }
            .flatMap { it?.let { Either.Right(it) } ?: Either.Left(NotFound("Fant ingen utkast med id: $id")) }
    }

    fun upsert(utkast: UtkastDbo): StatusResponse<UtkastDto> {
        return utkastRepository.upsert(utkast).map { it!! }
            .mapLeft { error -> ServerError("Klarte ikke lagre utkast med id: ${utkast.id}. Cause: $error") }
    }

    fun deleteUtkast(id: UUID): StatusResponse<Unit> {
        return utkastRepository.delete(id).map {}
            .mapLeft {
                ServerError(message = "Det oppsto en feil ved sletting av utkastet")
            }
    }

    fun getAll(filter: UtkastFilter): StatusResponse<List<UtkastDto>> {
        return utkastRepository.getAll(filter = filter).map { it }
            .mapLeft { error -> ServerError("Klarte ikke hente alle utkast med filter: $filter. Cause: $error") }
    }
}