package no.nav.mulighetsrommet.api.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prometheus.client.cache.caffeine.CacheMetricsCollector
import no.nav.mulighetsrommet.api.clients.brreg.BrregClient
import no.nav.mulighetsrommet.api.domain.dbo.toOverordnetEnhetDbo
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.api.repositories.VirksomhetRepository
import no.nav.mulighetsrommet.database.utils.getOrThrow
import no.nav.mulighetsrommet.metrics.Metrikker
import no.nav.mulighetsrommet.utils.CacheUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class VirksomhetService(
    private val brregClient: BrregClient,
    private val virksomhetRepository: VirksomhetRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val brregServiceCache: Cache<String, VirksomhetDto> = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.HOURS)
        .maximumSize(500)
        .recordStats()
        .build()

    init {
        val cacheMetrics: CacheMetricsCollector =
            CacheMetricsCollector().register(Metrikker.appMicrometerRegistry.prometheusRegistry)
        cacheMetrics.addCache("brregServiceCache", brregServiceCache)
    }

    suspend fun hentEnhet(orgnr: String): VirksomhetDto? {
        return virksomhetRepository.get(orgnr).getOrThrow() ?: syncEnhetFraBrreg(orgnr)
    }

    private suspend fun syncEnhetFraBrreg(orgnr: String): VirksomhetDto? {
        log.info("Skal synkronisere enhet med orgnr: $orgnr fra Brreg")
        val enhet = CacheUtils.tryCacheFirstNullable(brregServiceCache, orgnr) {
            brregClient.hentEnhet(orgnr)
        } ?: return null

        log.info("Hentet enhet fra Brreg med orgnr: $orgnr: $enhet")
        val overordnetEnhet = if (enhet.overordnetEnhet == null) {
            enhet
        } else {
            CacheUtils.tryCacheFirstNullable(brregServiceCache, enhet.overordnetEnhet) {
                brregClient.hentEnhet(enhet.overordnetEnhet)
            }
        } ?: return null
        log.debug("Potensiell overordnet enhet fra Brreg: $overordnetEnhet")

        if (overordnetEnhet.slettedato != null) {
            log.debug("Enhet med orgnr: ${enhet.organisasjonsnummer} er slettet i Brreg med slettedato ${enhet.slettedato}")
            return null
        }

        virksomhetRepository.upsertOverordnetEnhet(overordnetEnhet.toOverordnetEnhetDbo())
            .onLeft { log.warn("Feil ved upsert av virksomhet: $it") }

        return enhet
    }

    suspend fun sokEtterEnhet(sokestreng: String): List<VirksomhetDto> {
        return brregClient.sokEtterOverordnetEnheter(sokestreng)
    }
}