package no.nav.mulighetsrommet.api.routes.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import no.nav.mulighetsrommet.api.clients.pamOntologi.PamOntologiClient
import no.nav.mulighetsrommet.domain.dto.AmoKategorisering
import org.koin.ktor.ext.inject

fun Route.janzzRoutes() {
    val pam: PamOntologiClient by inject()

    get("janzz/sertifiseringer/sok") {
        val q: String by call.request.queryParameters

        val autoriseringer = async { pam.sokAutorisasjon(q) }
        val andreGodkjenninger = async { pam.sokAndreGodkjenninger(q) }

        val sertifiseringer = awaitAll(autoriseringer, andreGodkjenninger)
            .asSequence()
            .flatMap { typeaheads ->
                typeaheads.map {
                    AmoKategorisering.BransjeOgYrkesrettet.Sertifisering(
                        konseptId = it.konseptId,
                        label = it.label,
                    )
                }
            }
            // Det finnes noen konsepter med lik label som vi vil filtrere vekk. Det er ikke så
            // viktig hvilken som blir igjen, men sorterer først sånn at det alltid er den samme
            // som blir igjen.
            .sortedBy { it.konseptId }
            .sortedBy { it.label }
            .distinctBy { it.konseptId }
            .distinctBy { it.label }
            .toList()

        call.respond(sertifiseringer)
    }
}
