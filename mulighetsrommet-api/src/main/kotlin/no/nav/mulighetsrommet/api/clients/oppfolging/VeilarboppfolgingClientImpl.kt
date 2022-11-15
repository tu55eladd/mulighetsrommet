package no.nav.mulighetsrommet.api.clients.oppfolging

import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.mulighetsrommet.api.domain.ManuellStatusDTO
import no.nav.mulighetsrommet.api.domain.Oppfolgingsstatus
import no.nav.mulighetsrommet.api.setup.http.httpJsonClient
import no.nav.mulighetsrommet.secure_log.SecureLog
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(VeilarboppfolgingClientImpl::class.java)
private val secureLog = SecureLog.logger

class VeilarboppfolgingClientImpl(
    private val baseUrl: String,
    private val veilarboppfolgingTokenProvider: (accessToken: String) -> String,
    clientEngine: HttpClientEngine = CIO.create()
) : VeilarboppfolgingClient {
    val client = httpJsonClient(clientEngine).config {
        install(HttpCache)
    }
    override suspend fun hentOppfolgingsstatus(fnr: String, accessToken: String): Oppfolgingsstatus? {
        return try {
            val response = client.get("$baseUrl/person/$fnr/oppfolgingsstatus") {
                bearerAuth(
                    veilarboppfolgingTokenProvider.invoke(accessToken)
                )
            }

            if (response.status == HttpStatusCode.NotFound || response.status == HttpStatusCode.NoContent) {
                log.info("Fant ikke oppfølgingsstatus for bruker. Det kan være fordi bruker ikke er under oppfølging eller ikke finnes i Arena")
                return null
            }

            response.body<Oppfolgingsstatus>()
        } catch (exe: Exception) {
            secureLog.error("Klarte ikke hente oppfølgingsstatus for bruker med fnr: $fnr")
            log.error("Klarte ikke hente oppfølgingsstatus. Se secureLogs for detaljer.")
            null
        }
    }

    override suspend fun hentManuellStatus(fnr: String, accessToken: String): ManuellStatusDTO? {
        return try {
            val response = client.get("$baseUrl/v2/manuell/status?fnr=$fnr") {
                bearerAuth(
                    veilarboppfolgingTokenProvider.invoke(accessToken)
                )
            }

            if (response.status == HttpStatusCode.NotFound || response.status == HttpStatusCode.NoContent) {
                log.info("Fant ikke manuell status for bruker.")
                return null
            }

            val body = response.body<ManuellStatusDTO>()
            body
        } catch (exe: Exception) {
            secureLog.error("Klarte ikke hente manuell status for bruker med fnr: $fnr", exe)
            log.error("Klarte ikke hente manuell status. Se detaljer i secureLogs.")
            null
        }
    }
}
