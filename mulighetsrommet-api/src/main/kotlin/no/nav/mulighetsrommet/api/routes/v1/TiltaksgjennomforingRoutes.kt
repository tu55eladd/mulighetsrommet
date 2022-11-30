package no.nav.mulighetsrommet.api.routes.v1

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.routes.v1.responses.PaginatedResponse
import no.nav.mulighetsrommet.api.routes.v1.responses.Pagination
import no.nav.mulighetsrommet.api.utils.getPaginationParams
import no.nav.mulighetsrommet.api.utils.toUUID
import org.koin.ktor.ext.inject

fun Route.tiltaksgjennomforingRoutes() {
    val tiltaksgjennomforinger: TiltaksgjennomforingRepository by inject()

    route("/api/v1/tiltaksgjennomforinger") {
        get {
            val paginationParams = getPaginationParams()
            val (totalCount, items) = tiltaksgjennomforinger.getTiltaksgjennomforinger(
                paginationParams
            )
            call.respond(
                PaginatedResponse(
                    pagination = Pagination(
                        totalCount = totalCount,
                        currentPage = paginationParams.page,
                        pageSize = paginationParams.limit
                    ),
                    data = items
                )
            )
        }

        get("{id}") {
            val id = call.parameters["id"]?.toUUID() ?: return@get call.respondText(
                "Mangler eller ugyldig id",
                status = HttpStatusCode.BadRequest
            )
            val tiltaksgjennomforing =
                tiltaksgjennomforinger.getTiltaksgjennomforingById(id) ?: return@get call.respondText(
                    "Det finnes ikke noe tiltaksgjennomføring med id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(tiltaksgjennomforing)
        }
    }
}
