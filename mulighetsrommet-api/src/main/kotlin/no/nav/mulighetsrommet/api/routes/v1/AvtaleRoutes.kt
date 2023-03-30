package no.nav.mulighetsrommet.api.routes.v1

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.services.AvtaleService
import no.nav.mulighetsrommet.api.utils.getAvtaleFilter
import no.nav.mulighetsrommet.api.utils.getPaginationParams
import no.nav.mulighetsrommet.domain.dbo.Avslutningsstatus
import no.nav.mulighetsrommet.domain.dbo.AvtaleDbo
import no.nav.mulighetsrommet.domain.dto.Avtaletype
import no.nav.mulighetsrommet.domain.serializers.LocalDateSerializer
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import no.nav.mulighetsrommet.utils.toUUID
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.util.*

fun Route.avtaleRoutes() {
    val avtaler: AvtaleService by inject()

    val logger = application.environment.log

    route("/api/v1/internal/avtaler") {
        get {
            val pagination = getPaginationParams()
            val filter = getAvtaleFilter()
            val result = avtaler.getAll(filter, pagination)

            call.respond(result)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toUUID() ?: return@get call.respondText(
                text = "Mangler eller ugyldig id",
                status = HttpStatusCode.BadRequest,
            )

            val avtale = avtaler.get(id) ?: return@get call.respondText(
                text = "Det finnes ikke noen avtale med id $id",
                status = HttpStatusCode.NotFound,
            )

            call.respond(avtale)
        }

        get("{id}/nokkeltall") {
            val id = call.parameters["id"]?.toUUID() ?: return@get call.respondText(
                text = "Mangler eller ugyldig id",
                status = HttpStatusCode.BadRequest,
            )

            val nokkeltall = avtaler.getNokkeltallForAvtaleMedId(id)

            call.respond(nokkeltall)
        }

        put {
            val avtaleRequest = call.receive<AvtaleRequest>()

            avtaler.upsert(avtaleRequest)
                .map { call.respond(it) }
                .mapLeft {
                    logError(logger, it.error)
                    call.respond(HttpStatusCode.InternalServerError, "Kunne ikke opprette avtale")
                }
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toUUID() ?: return@delete call.respondText(
                "Mangler eller ugyldig id",
                status = HttpStatusCode.BadRequest
            )

            avtaler.delete(id)
                .map { call.response.status(HttpStatusCode.OK) }
                .mapLeft {
                    logError(logger, it.error)
                    call.respond(HttpStatusCode.InternalServerError, "Kunne ikke slette avtale")
                }
        }
    }
}

@Serializable
data class AvtaleRequest(
    val navn: String,
    @Serializable(with = UUIDSerializer::class)
    val tiltakstypeId: UUID,
    val leverandorOrganisasjonsnummer: String,
    @Serializable(with = LocalDateSerializer::class)
    val startDato: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val sluttDato: LocalDate,
    val enhet: String,
    val antallPlasser: Int,
    val url: String,
    val ansvarlig: String
) {
    fun toDbo(): AvtaleDbo {
        return AvtaleDbo(
            id = UUID.randomUUID(),
            navn = navn,
            tiltakstypeId = tiltakstypeId,
            leverandorOrganisasjonsnummer = leverandorOrganisasjonsnummer,
            startDato = startDato,
            sluttDato = sluttDato,
            enhet = enhet,
            avtaletype = Avtaletype.Forhaandsgodkjent,
            avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
            antallPlasser = antallPlasser,
            url = url,
            opphav = AvtaleDbo.Opphav.MULIGHETSROMMET,
            ansvarlig = ansvarlig
        )
    }
}
