package no.nav.mulighetsrommet.api.services

import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.clients.oppfolging.VeilarboppfolgingClient
import no.nav.mulighetsrommet.api.clients.person.VeilarbpersonClient
import no.nav.mulighetsrommet.api.clients.vedtak.VeilarbvedtaksstotteClient
import no.nav.mulighetsrommet.api.domain.Innsatsgruppe
import no.nav.mulighetsrommet.api.domain.ManuellStatusDTO
import no.nav.mulighetsrommet.api.domain.Oppfolgingsenhet

class BrukerService(
    private val veilarboppfolgingClient: VeilarboppfolgingClient,
    private val veilarbvedtaksstotteClient: VeilarbvedtaksstotteClient,
    private val veilarbpersonClient: VeilarbpersonClient
) {

    suspend fun hentBrukerdata(fnr: String, accessToken: String): Brukerdata {
        val oppfolgingsstatus = veilarboppfolgingClient.hentOppfolgingsstatus(fnr, accessToken)
        val manuellStatus = veilarboppfolgingClient.hentManuellStatus(fnr, accessToken)
        val sisteVedtak = veilarbvedtaksstotteClient.hentSiste14AVedtak(fnr, accessToken)
        val personInfo = veilarbpersonClient.hentPersonInfo(fnr, accessToken)

        return Brukerdata(
            fnr = fnr,
            oppfolgingsenhet = oppfolgingsstatus?.oppfolgingsenhet,
            servicegruppe = oppfolgingsstatus?.servicegruppe,
            innsatsgruppe = sisteVedtak?.innsatsgruppe,
            fornavn = personInfo?.fornavn,
            manuellStatus = manuellStatus
        )
    }

    @Serializable
    data class Brukerdata(
        val fnr: String,
        val innsatsgruppe: Innsatsgruppe?,
        val oppfolgingsenhet: Oppfolgingsenhet?,
        val servicegruppe: String?,
        val fornavn: String?,
        val manuellStatus: ManuellStatusDTO?
    )
}
