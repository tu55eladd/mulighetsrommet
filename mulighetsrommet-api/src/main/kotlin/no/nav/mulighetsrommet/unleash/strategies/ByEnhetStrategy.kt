package no.nav.mulighetsrommet.unleash.strategies

import io.getunleash.UnleashContext
import io.getunleash.strategy.Strategy
import no.nav.common.types.identer.NavIdent
import no.nav.mulighetsrommet.api.services.AxsysService
import no.nav.mulighetsrommet.securelog.SecureLog
import org.slf4j.LoggerFactory
import java.util.*

class ByEnhetStrategy(private val axsysService: AxsysService) : Strategy {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val PARAM = "valgtEnhet"
    private val TEMA_OPPFOLGING = "OPP"
    override fun getName(): String {
        return "byEnhet"
    }

    override fun isEnabled(parameters: MutableMap<String, String>): Boolean {
        return false
    }

    override fun isEnabled(parameters: MutableMap<String, String>, context: UnleashContext): Boolean {
        return context.userId
            .flatMap { userId ->
                Optional.ofNullable(parameters.get(PARAM))
                    .map { enheter -> enheter.split(",\\s?".toRegex()) }
                    .map { enabledEnheter -> enabledEnheter.intersect(brukersEnheter(userId).toSet()).isNotEmpty() }
            }.orElse(false)
    }

    private fun brukersEnheter(navIdent: String): List<String?> {
        if (!erNavIdent(navIdent)) {
            logger.warn("Fikk ident som ikke er en NAVident. Om man ser mye av denne feilen bør man utforske hvorfor.")
            return emptyList<String>()
        }
        return hentEnheter(navIdent)
    }

    private fun hentEnheter(navIdent: String): List<String?> {
        return try {
            axsysService.get().hentTilganger(NavIdent(navIdent)).stream()
                .filter { enhet ->
                    enhet.temaer.contains(TEMA_OPPFOLGING)
                }
                .map { enhet ->
                    enhet.enhetId.get()
                }.toList()
        } catch (exe: Exception) {
            logger.warn("Klarte ikke hente tilganger fra Axsys. Se secureLogs for mer informasjon")
            SecureLog.logger.warn("Klart eikke hente tilganger fra Axsys for bruker med ident: $navIdent. Error: $exe")
            emptyList()
        }
    }

    private fun erNavIdent(verdi: String): Boolean {
        return verdi.matches("\\w\\d{6}".toRegex())
    }
}