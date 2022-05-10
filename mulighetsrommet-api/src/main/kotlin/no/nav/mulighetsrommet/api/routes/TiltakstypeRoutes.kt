package no.nav.mulighetsrommet.api.routes

import io.ktor.http.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.mulighetsrommet.api.services.TiltaksgjennomforingService
import no.nav.mulighetsrommet.api.services.TiltakstypeService
import no.nav.mulighetsrommet.domain.Tiltakskode
import no.nav.mulighetsrommet.domain.Tiltakstype
import org.koin.ktor.ext.inject

// TODO: Må lage noe felles validering her etterhvert
fun Parameters.parseList(parameter: String): List<String> {
    return entries().filter { it.key == parameter }.flatMap { it.value }
}

fun Route.tiltakstypeRoutes() {

    val tiltakstypeService: TiltakstypeService by inject()
    val tiltaksgjennomforingService: TiltaksgjennomforingService by inject()

    get("/api/tiltakstyper") {
        val search = call.request.queryParameters["search"]

        val innsatsgrupper = call.request.queryParameters.parseList("innsatsgrupper").map { Integer.parseInt(it) }

        val items = tiltakstypeService.getTiltakstyper(innsatsgrupper, search)
        call.respond(items)
    }
    get("/api/tiltakstyper/{tiltakskode}") {
        runCatching {
            val tiltakskode = Tiltakskode.valueOf(call.parameters["tiltakskode"]!!)
            tiltakstypeService.getTiltakstypeByTiltakskode(tiltakskode)
        }.onSuccess { fetchedTiltakstype ->
            if (fetchedTiltakstype != null) {
                call.respond(fetchedTiltakstype)
            }
            call.respondText(text = "Fant ikke tiltakstype", status = HttpStatusCode.NotFound)
        }.onFailure {
            call.application.environment.log.error(it.stackTraceToString())
            call.respondText(text = "Fant ikke tiltakstype", status = HttpStatusCode.NotFound)
        }
    }
    get("/api/tiltakstyper/{tiltakskode}/tiltaksgjennomforinger") {
        runCatching {
            val tiltakskode = Tiltakskode.valueOf(call.parameters["tiltakskode"]!!)
            tiltaksgjennomforingService.getTiltaksgjennomforingerByTiltakskode(tiltakskode)
        }.onSuccess { fetchedTiltaksgjennomforinger ->
            call.respond(fetchedTiltaksgjennomforinger)
        }.onFailure { call.respondText("Fant ikke tiltakstype", status = HttpStatusCode.NotFound) }
    }
    post("/api/tiltakstyper") {
        runCatching {
            val tiltakstype = call.receive<Tiltakstype>()
            tiltakstypeService.createTiltakstype(tiltakstype)
        }.onSuccess { createdTiltakstype ->
            call.response.status(HttpStatusCode.Created)
            call.respond(createdTiltakstype)
        }.onFailure {
            call.respondText("Kunne ikke opprette tiltakstype", status = HttpStatusCode.InternalServerError)
        }
    }
    put("/api/tiltakstyper/{tiltakskode}") {
        runCatching {
            val tiltakskode = Tiltakskode.valueOf(call.parameters["tiltakskode"]!!)
            val tiltakstype = call.receive<Tiltakstype>()
            tiltakstypeService.updateTiltakstype(tiltakskode, tiltakstype)
        }.onSuccess { updatedTiltakstype ->
            call.respond(updatedTiltakstype)
        }
    }
}
