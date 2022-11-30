package no.nav.mulighetsrommet.api.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prometheus.client.cache.caffeine.CacheMetricsCollector
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.clients.oppfolging.VeilarboppfolgingClient
import no.nav.mulighetsrommet.api.clients.person.VeilarbpersonClient
import no.nav.mulighetsrommet.api.clients.vedtak.VeilarbvedtaksstotteClient
import no.nav.mulighetsrommet.api.domain.Innsatsgruppe
import no.nav.mulighetsrommet.api.domain.ManuellStatusDTO
import no.nav.mulighetsrommet.api.domain.Oppfolgingsenhet
import no.nav.mulighetsrommet.ktor.plugins.Metrikker
import no.nav.mulighetsrommet.utils.CacheUtils
import java.util.concurrent.TimeUnit

class BrukerService(
    private val veilarboppfolgingClient: VeilarboppfolgingClient,
    private val veilarbvedtaksstotteClient: VeilarbvedtaksstotteClient,
    private val veilarbpersonClient: VeilarbpersonClient
) {

    val brukerCache: Cache<String, Brukerdata> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(500)
        .recordStats()
        .build()

    init {
        val cacheMetrics: CacheMetricsCollector =
            CacheMetricsCollector().register(Metrikker.appMicrometerRegistry.prometheusRegistry)
        cacheMetrics.addCache("brukerCache", brukerCache)
    }

    suspend fun hentBrukerdata(fnr: String, accessToken: String): Brukerdata {
        return CacheUtils.tryCacheFirstNotNull(brukerCache, fnr) {
            val oppfolgingsenhet = veilarboppfolgingClient.hentOppfolgingsstatus(fnr, accessToken)
            val manuellStatus = veilarboppfolgingClient.hentManuellStatus(fnr, accessToken)
            val sisteVedtak = veilarbvedtaksstotteClient.hentSiste14AVedtak(fnr, accessToken)
            val personInfo = veilarbpersonClient.hentPersonInfo(fnr, accessToken)

            Brukerdata(
                fnr = fnr,
                oppfolgingsenhet = oppfolgingsenhet?.oppfolgingsenhet,
                innsatsgruppe = sisteVedtak?.innsatsgruppe,
                fornavn = personInfo?.fornavn,
                manuellStatus = manuellStatus
            )
        }
    }

    @Serializable
    data class Brukerdata(
        val fnr: String,
        val innsatsgruppe: Innsatsgruppe?,
        val oppfolgingsenhet: Oppfolgingsenhet?,
        val fornavn: String?,
        val manuellStatus: ManuellStatusDTO?
    )
}
