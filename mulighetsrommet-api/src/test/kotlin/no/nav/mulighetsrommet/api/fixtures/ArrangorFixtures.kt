package no.nav.mulighetsrommet.api.fixtures

import no.nav.mulighetsrommet.api.domain.dto.ArrangorDto
import java.util.*

object ArrangorFixtures {
    object Fretex {
        val hovedenhet = ArrangorDto(
            id = UUID.randomUUID(),
            organisasjonsnummer = "983982433",
            navn = "FRETEX AS",
            postnummer = "4315",
            poststed = "SANDNES",
        )

        val underenhet1 = ArrangorDto(
            id = UUID.randomUUID(),
            organisasjonsnummer = "992943084",
            overordnetEnhet = "983982433",
            navn = "FRETEX AS AVD OSLO",
            postnummer = "0668",
            poststed = "OSLO",
        )
    }

    val hovedenhet = ArrangorDto(
        id = UUID.randomUUID(),
        organisasjonsnummer = "123456789",
        navn = "Hovedenhet AS",
        postnummer = "0102",
        poststed = "Oslo",
    )

    val underenhet1 = ArrangorDto(
        id = UUID.randomUUID(),
        organisasjonsnummer = "976663934",
        overordnetEnhet = "123456789",
        navn = "Underenhet 1 AS",
        postnummer = "0103",
        poststed = "Oslo",
    )

    val underenhet2 = ArrangorDto(
        id = UUID.randomUUID(),
        organisasjonsnummer = "890765789",
        overordnetEnhet = "123456789",
        navn = "Underenhet 2 AS",
        postnummer = "0201",
        poststed = "Lillestrøm",
    )
}
