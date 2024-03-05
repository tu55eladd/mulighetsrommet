package no.nav.mulighetsrommet.api.routes.v1

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.clients.brreg.BrregClient
import no.nav.mulighetsrommet.api.clients.brreg.BrregError
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetKontaktperson
import no.nav.mulighetsrommet.api.repositories.VirksomhetRepository
import no.nav.mulighetsrommet.api.routes.v1.responses.*
import no.nav.mulighetsrommet.api.services.VirksomhetService
import no.nav.mulighetsrommet.api.utils.getVirksomhetFilter
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import org.koin.ktor.ext.inject
import java.util.*

fun Route.virksomhetRoutes() {
    val virksomhetRepository: VirksomhetRepository by inject()
    val virksomhetService: VirksomhetService by inject()
    val brregClient: BrregClient by inject()

    route("api/v1/internal/virksomhet") {
        get {
            val filter = getVirksomhetFilter()
            call.respond(virksomhetRepository.getAll(til = filter.til))
        }

        get("sok") {
            val sok: String by call.request.queryParameters

            if (sok.isBlank()) {
                throw BadRequestException("'sok' kan ikke være en tom streng")
            }

            val response = brregClient.sokEtterOverordnetEnheter(sok)
                .map {
                    // Kombinerer resultat med utenlandske virksomheter siden de ikke finnes i brreg
                    it + virksomhetRepository.getAll(sok = sok, utenlandsk = true).map { virksomhet ->
                        VirksomhetDto(
                            organisasjonsnummer = virksomhet.organisasjonsnummer,
                            navn = virksomhet.navn,
                            overordnetEnhet = virksomhet.overordnetEnhet,
                            underenheter = listOf(),
                            postnummer = virksomhet.postnummer,
                            poststed = virksomhet.poststed,
                            slettetDato = virksomhet.slettetDato,
                        )
                    }
                }
                .mapLeft { toStatusResponseError(it) }

            call.respondWithStatusResponse(response)
        }

        get("{id}") {
            val id: UUID by call.parameters

            call.respond(virksomhetRepository.getById(id))
        }

        get("{id}/kontaktpersoner") {
            val id: UUID by call.parameters

            call.respond(virksomhetService.hentKontaktpersoner(id))
        }

        post("{orgnr}") {
            val orgnr = call.parameters.getOrFail("orgnr").also { validateOrgnr(it) }

            if (isUtenlandskOrgnr(orgnr)) {
                val virksomhet = virksomhetRepository.get(orgnr)
                return@post if (virksomhet != null) {
                    call.respond(virksomhet)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Fant ikke enhet med orgnr: $orgnr")
                }
            }

            val response = virksomhetService.getOrSyncVirksomhetFromBrreg(orgnr)
                .mapLeft { toStatusResponseError(it) }

            call.respondWithStatusResponse(response)
        }

        put("{id}/kontaktpersoner") {
            val id: UUID by call.parameters
            val virksomhetKontaktperson = call.receive<VirksomhetKontaktpersonRequest>()

            val result = virksomhetKontaktperson.toDto(id)
                .map { virksomhetService.upsertKontaktperson(it) }
                .onLeft { application.log.warn("Klarte ikke opprette kontaktperson: $it") }

            call.respondWithStatusResponse(result)
        }

        delete("kontaktperson/{id}") {
            val id: UUID by call.parameters

            call.respondWithStatusResponse(virksomhetService.deleteKontaktperson(id))
        }

        post("/update") {
            val orgnr = call.parameters.getOrFail("orgnr").also { validateOrgnr(it) }

            virksomhetService.syncHovedenhetFromBrreg(orgnr)
                .onRight { virksomhet ->
                    call.respond("${virksomhet.navn} oppdatert")
                }
                .onLeft { error ->
                    call.respondWithStatusResponseError(toStatusResponseError(error))
                }
        }
    }
}

fun validateOrgnr(orgnr: String) {
    if (!orgnr.matches("^[0-9]{9}\$".toRegex())) {
        throw BadRequestException("Verdi sendt inn er ikke et organisasjonsnummer. Organisasjonsnummer er 9 siffer og bare tall.")
    }
}

fun isUtenlandskOrgnr(orgnr: String): Boolean {
    return orgnr.matches("^[1-7][0-9]{8}\$".toRegex())
}

private fun toStatusResponseError(it: BrregError) = when (it) {
    BrregError.NotFound -> NotFound()
    BrregError.BadRequest -> BadRequest()
    BrregError.Error -> ServerError()
}

@Serializable
data class VirksomhetKontaktpersonRequest(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val navn: String,
    val telefon: String?,
    val beskrivelse: String?,
    val epost: String,
) {
    fun toDto(virksomhetId: UUID): StatusResponse<VirksomhetKontaktperson> {
        val navn = navn.trim()
        val epost = epost.trim()

        val errors = buildList {
            if (navn.isEmpty()) {
                add(ValidationError.of(VirksomhetKontaktperson::navn, "Navn er påkrevd"))
            }
            if (epost.isEmpty()) {
                add(ValidationError.of(VirksomhetKontaktperson::epost, "E-post er påkrevd"))
            }
        }

        if (errors.isNotEmpty()) {
            return Either.Left(BadRequest(errors = errors))
        }

        return Either.Right(
            VirksomhetKontaktperson(
                id = id,
                virksomhetId = virksomhetId,
                navn = navn,
                telefon = telefon?.trim()?.ifEmpty { null },
                epost = epost,
                beskrivelse = beskrivelse?.trim()?.ifEmpty { null },
            ),
        )
    }
}
