package no.nav.mulighetsrommet.api.routes.v1

import arrow.core.toNonEmptyListOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.clients.sanity.SanityPerspective
import no.nav.mulighetsrommet.api.domain.dto.Oppskrift
import no.nav.mulighetsrommet.api.domain.dto.Oppskrifter
import no.nav.mulighetsrommet.api.domain.dto.VeilederflateTiltaksgjennomforing
import no.nav.mulighetsrommet.api.plugins.getNavAnsattAzureId
import no.nav.mulighetsrommet.api.services.PoaoTilgangService
import no.nav.mulighetsrommet.api.services.VeilederflateService
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import org.koin.ktor.ext.inject
import java.util.*

fun Route.veilederflateRoutes() {
    val veilederflateService: VeilederflateService by inject()
    val poaoTilgangService: PoaoTilgangService by inject()

    route("/api/v1/internal/veileder") {
        get("/innsatsgrupper") {
            poaoTilgangService.verifyAccessToModia(getNavAnsattAzureId())

            val innsatsgrupper = veilederflateService.hentInnsatsgrupper()

            call.respond(innsatsgrupper)
        }

        get("/tiltakstyper") {
            poaoTilgangService.verifyAccessToModia(getNavAnsattAzureId())

            val tiltakstyper = veilederflateService.hentTiltakstyper()

            call.respond(tiltakstyper)
        }

        post("/tiltaksgjennomforinger") {
            val request = call.receive<GetTiltaksgjennomforingerRequest>()
            val enheter = request.enheter.toNonEmptyListOrNull()
                ?: return@post call.respond(emptyList<VeilederflateTiltaksgjennomforing>())

            val result = veilederflateService.hentTiltaksgjennomforinger(
                enheter = enheter,
                innsatsgruppe = request.innsatsgruppe,
                tiltakstypeIds = request.tiltakstypeIds,
                search = request.search,
                apentForInnsok = request.apentForInnsok,
            )

            call.respond(result)
        }

        post("/tiltaksgjennomforing") {
            val request = call.receive<GetTiltaksgjennomforingRequest>()
            val enheter = request.enheter.toNonEmptyListOrNull()
                ?: return@post call.respond(HttpStatusCode.NotFound)

            val result = veilederflateService.hentTiltaksgjennomforing(
                request.id,
                enheter,
                SanityPerspective.PUBLISHED,
            )

            call.respond(result)
        }

        get("/oppskrifter/{tiltakstypeId}") {
            val tiltakstypeId = call.parameters.getOrFail("tiltakstypeId")
            val perspective = call.request.queryParameters["perspective"]?.let {
                when (it) {
                    "published" -> SanityPerspective.PUBLISHED
                    "raw" -> SanityPerspective.RAW
                    else -> SanityPerspective.PREVIEW_DRAFTS
                }
            }
                ?: SanityPerspective.PUBLISHED
            val oppskrifter: List<Oppskrift> =
                veilederflateService.hentOppskrifterForTiltakstype(tiltakstypeId, perspective)
            call.respond(Oppskrifter(data = oppskrifter))
        }
    }
}

@Serializable
data class GetTiltaksgjennomforingerRequest(
    val enheter: List<String>,
    val innsatsgruppe: String? = null,
    val tiltakstypeIds: List<String>? = null,
    val search: String? = null,
    val apentForInnsok: ApentForInnsok,
)

enum class ApentForInnsok {
    APENT,
    STENGT,
    APENT_ELLER_STENGT,
}

@Serializable
data class GetTiltaksgjennomforingRequest(
    val enheter: List<String>,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
)
