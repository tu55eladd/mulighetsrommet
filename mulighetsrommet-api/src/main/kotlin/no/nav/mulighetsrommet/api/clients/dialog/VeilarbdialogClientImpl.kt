package no.nav.mulighetsrommet.api.clients.dialog

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.mulighetsrommet.api.clients.oppfolging.VeilarboppfolgingClientImpl
import no.nav.mulighetsrommet.api.services.DialogRequest
import no.nav.mulighetsrommet.api.services.DialogResponse
import no.nav.mulighetsrommet.api.setup.http.baseClient
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(VeilarboppfolgingClientImpl::class.java)

class VeilarbdialogClientImpl(
    private val baseUrl: String,
    private val client: HttpClient = baseClient
) : VeilarbdialogClient {

    override suspend fun sendMeldingTilDialogen(fnr: String, requestBody: DialogRequest): DialogResponse? {
        return try {
            val response = client.post("$baseUrl/dialog?fnr=$fnr") {
                header("Nav-Consumer-Id", "mulighetsrommet-api")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.Conflict) {
                log.info("Kan ikke sende melding til dialogen, bruker oppfyller ikke kravene for digital kommunikasjon")
                return null
            }

            return response.body<DialogResponse>()
        } catch (exe: Exception) {
            log.error("Klarte ikke sende melding til dialogen: {}", exe)
            null
        }
    }
}
