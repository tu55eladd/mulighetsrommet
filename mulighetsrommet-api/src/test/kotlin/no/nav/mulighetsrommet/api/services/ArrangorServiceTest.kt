package no.nav.mulighetsrommet.api.services

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.mulighetsrommet.api.clients.enhetsregister.AmtEnhetsregisterClient
import no.nav.mulighetsrommet.api.domain.VirksomhetDTO

class ArrangorServiceTest : FunSpec({

    val amtEnhetsregister: AmtEnhetsregisterClient = mockk()

    val arrangorService = ArrangorService(amtEnhetsregister)

    beforeSpec {
        coEvery { amtEnhetsregister.hentVirksomhet(111) } returns VirksomhetDTO(
            organisasjonsnummer = "789",
            navn = "Bedrift 1",
            overordnetEnhetOrganisasjonsnummer = "1011",
            overordnetEnhetNavn = "Overordnetbedrift 1"
        )
        coEvery { amtEnhetsregister.hentVirksomhet(222) } returns VirksomhetDTO(
            organisasjonsnummer = "7891",
            navn = "Bedrift 2",
            overordnetEnhetOrganisasjonsnummer = "1011",
            overordnetEnhetNavn = "Overordnetbedrift 2"
        )
    }

    test("henter navn på arrangør basert på virksomhetsnummer tilhørende arrangør id") {
        arrangorService.hentArrangornavn("111") shouldBe "Overordnetbedrift 1"
        arrangorService.hentArrangornavn("222") shouldBe "Overordnetbedrift 2"
    }

    test("arrangør navn blir cachet basert på arrangør id") {
        arrangorService.hentArrangornavn("111")
        arrangorService.hentArrangornavn("222")
        arrangorService.hentArrangornavn("222")
        arrangorService.hentArrangornavn("222")
        arrangorService.hentArrangornavn("111")

        coVerify(exactly = 2) { amtEnhetsregister.hentVirksomhet(any()) }
    }
})
