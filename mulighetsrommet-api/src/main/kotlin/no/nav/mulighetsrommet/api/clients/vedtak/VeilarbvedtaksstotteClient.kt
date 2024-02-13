package no.nav.mulighetsrommet.api.clients.vedtak

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.prometheus.client.cache.caffeine.CacheMetricsCollector
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.ktor.clients.httpJsonClient
import no.nav.mulighetsrommet.metrics.Metrikker
import no.nav.mulighetsrommet.securelog.SecureLog
import no.nav.mulighetsrommet.serialization.json.JsonIgnoreUnknownKeys
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class VeilarbvedtaksstotteClient(
    private val baseUrl: String,
    private val tokenProvider: (accessToken: String) -> String,
    clientEngine: HttpClientEngine = CIO.create(),
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val client = httpJsonClient(clientEngine).config {
        install(HttpCache)
    }

    private val siste14aVedtakCache: Cache<String, VedtakDto> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(10_000)
        .recordStats()
        .build()

    init {
        val cacheMetrics: CacheMetricsCollector =
            CacheMetricsCollector().register(Metrikker.appMicrometerRegistry.prometheusRegistry)
        cacheMetrics.addCache("siste14aVedtakCache", siste14aVedtakCache)
    }

    suspend fun hentSiste14AVedtak(fnr: String, accessToken: String): Either<VedtakError, VedtakDto> {
        siste14aVedtakCache.getIfPresent(fnr)?.let { return@hentSiste14AVedtak it.right() }

        val response = client.post("$baseUrl/v2/hent-siste-14a-vedtak") {
            bearerAuth(tokenProvider.invoke(accessToken))
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(VedtakRequest(fnr = fnr))
        }

        return if (response.status == HttpStatusCode.Forbidden) {
            log.warn("Mangler tilgang til å hente siste 14A-vedtak for bruker. Har innlogget personen riktig AD-rolle for å hente siste 14A-vedtak?")
            VedtakError.Forbidden.left()
        } else {
            if (!response.status.isSuccess()) {
                SecureLog.logger.error("Klarte ikke hente siste 14A-vedtak. Response: $response")
                log.error("Klarte ikke hente siste 14A-vedtak. Status: ${response.status}")
                VedtakError.Error.left()
            } else {
                val body = response.bodyAsText()
                if (body.isBlank()) {
                    log.info("Fant ikke siste 14A-vedtak for bruker")
                    VedtakError.NotFound.left()
                } else {
                    JsonIgnoreUnknownKeys.decodeFromString<VedtakDto>(body).right()
                }
            }
                .onRight { siste14aVedtakCache.put(fnr, it) }
        }
    }
}

enum class VedtakError {
    NotFound,
    Forbidden,
    Error,
}

@Serializable
data class VedtakRequest(
    val fnr: String,
)
