package no.nav.mulighetsrommet.api.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.http.*
import io.prometheus.client.cache.caffeine.CacheMetricsCollector
import no.nav.mulighetsrommet.ktor.exception.StatusException
import no.nav.mulighetsrommet.ktor.plugins.Metrikker
import no.nav.mulighetsrommet.secure_log.SecureLog
import no.nav.mulighetsrommet.utils.CacheUtils
import no.nav.poao_tilgang.client.*
import java.util.*
import java.util.concurrent.TimeUnit

class PoaoTilgangService(
    val client: PoaoTilgangClient
) {

    private val tilgangCache: Cache<String, Boolean> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(10_000)
        .recordStats()
        .build()

    private val brukerAzureIdToAdGruppeCache: Cache<String, List<AdGruppe>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(10_000)
        .recordStats()
        .build()

    init {
        val cacheMetrics: CacheMetricsCollector = CacheMetricsCollector().register(Metrikker.appMicrometerRegistry.prometheusRegistry)
        cacheMetrics.addCache("brukerAzureIdToAdGruppeCache", brukerAzureIdToAdGruppeCache)
        cacheMetrics.addCache("tilgangCache", tilgangCache)
    }

    fun verifyAccessToUserFromVeileder(navAnsattAzureId: UUID, norskIdent: String) {
        val access = CacheUtils.tryCacheFirstNotNull(tilgangCache, "$navAnsattAzureId-$norskIdent") {
            // TODO Hør med Sondre ang. error handling ved kasting av feil
            client.evaluatePolicy(
                NavAnsattTilgangTilEksternBrukerPolicyInput(
                    navAnsattAzureId,
                    TilgangType.LESE,
                    norskIdent
                )
            )
                .getOrThrow().isPermit
        }

        if (!access) {
            throw StatusException(HttpStatusCode.Forbidden, "Veileder mangler tilgang til bruker")
        }
    }

    fun verfiyAccessToModia(navAnsattAzureId: UUID) {
        val access = CacheUtils.tryCacheFirstNotNull(tilgangCache, navAnsattAzureId.toString()) {
            client.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(navAnsattAzureId)).getOrThrow().isPermit
        }

        if (!access) {
            SecureLog.logger.warn("Veileder med navAnsattAzureId $navAnsattAzureId har ikke tilgang til modia")
            throw StatusException(HttpStatusCode.Forbidden, "Veileder har ikke tilgang til modia, se mer i secure logs")
        }
    }

    fun hentAdGrupper(navAnsattAzureId: UUID): List<AdGruppe> {
        return CacheUtils.tryCacheFirstNotNull(brukerAzureIdToAdGruppeCache, navAnsattAzureId.toString()) {
            client.hentAdGrupper(navAnsattAzureId).getOrDefault { emptyList() }
        }
    }
}
