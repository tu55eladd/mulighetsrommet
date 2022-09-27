package no.nav.mulighetsrommet.api.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.http.*
import no.nav.mulighetsrommet.ktor.exception.StatusException
import no.nav.poao_tilgang.client.EksternBrukerPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangClient
import java.util.concurrent.TimeUnit

class PoaoTilgangService(
    val client: PoaoTilgangClient
) {

    private val cache: Cache<String, Boolean> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(10_000)
        .build()

    suspend fun verifyAccessToUserFromVeileder(navIdent: String, norskIdent: String) {
        // TODO Se på nøkkel for cache-verdi
        val access = cachedResult(cache, navIdent) {
            // TODO Hør med Sondre ang. error handling ved kasting av feil
            client.evaluatePolicy(EksternBrukerPolicyInput(navIdent, norskIdent)).getOrThrow().isPermit
        }

        if (!access) {
            throw StatusException(HttpStatusCode.Forbidden, "Mangler tilgang til Modia Arbeidsrettet oppfølging")
        }
    }

    private suspend fun <K, V : Any> cachedResult(
        cache: Cache<K, V>,
        key: K,
        supplier: suspend () -> V
    ): V {
        val cachedValue = cache.getIfPresent(key)
        if (cachedValue != null) {
            return cachedValue
        }

        val value = supplier.invoke()
        cache.put(key, value)
        return value
    }
}
