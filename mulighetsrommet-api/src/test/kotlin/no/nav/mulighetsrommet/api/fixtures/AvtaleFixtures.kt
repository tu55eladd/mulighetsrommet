package no.nav.mulighetsrommet.api.fixtures

import no.nav.mulighetsrommet.api.domain.dbo.AvtaleDbo
import no.nav.mulighetsrommet.api.routes.v1.AvtaleRequest
import no.nav.mulighetsrommet.domain.constants.ArenaMigrering
import no.nav.mulighetsrommet.domain.dto.*
import java.time.LocalDate
import java.util.*

object AvtaleFixtures {
    val avtale1 = AvtaleDbo(
        id = UUID.randomUUID(),
        navn = "Avtalenavn",
        avtalenummer = "2023#1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        leverandorOrganisasjonsnummer = "123456789",
        leverandorUnderenheter = emptyList(),
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 28),
        avtaletype = Avtaletype.Rammeavtale,
        prisbetingelser = null,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        administratorer = emptyList(),
        navEnheter = listOf("2990"),
        leverandorKontaktpersonId = null,
        antallPlasser = null,
        url = null,
        updatedAt = LocalDate.now().atStartOfDay(),
    )

    val avtaleRequest = AvtaleRequest(
        id = UUID.randomUUID(),
        navn = "Avtalenavn",
        avtalenummer = "2023#1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        leverandorOrganisasjonsnummer = "123456789",
        leverandorUnderenheter = listOf("123456789"),
        startDato = LocalDate.of(2023, 1, 11),
        sluttDato = LocalDate.of(2023, 2, 28),
        avtaletype = Avtaletype.Rammeavtale,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        administratorer = listOf("DD1"),
        url = "google.com",
        navEnheter = listOf("2990"),
        leverandorKontaktpersonId = null,
        prisbetingelser = null,
    )

    val oppfolgingAvtaleAdminDto = AvtaleAdminDto(
        id = avtale1.id,
        tiltakstype = AvtaleAdminDto.Tiltakstype(
            id = TiltakstypeFixtures.Oppfolging.id,
            navn = TiltakstypeFixtures.Oppfolging.navn,
            arenaKode = TiltakstypeFixtures.Oppfolging.tiltakskode,
        ),
        navn = avtale1.navn,
        avtalenummer = avtale1.navn,
        leverandor = AvtaleAdminDto.Leverandor(organisasjonsnummer = "123456789", navn = "Bedrift", slettet = false),
        leverandorUnderenheter = listOf(
            AvtaleAdminDto.LeverandorUnderenhet(organisasjonsnummer = "976663934", navn = "Bedrift underenhet"),
        ),
        leverandorKontaktperson = VirksomhetKontaktperson(
            id = UUID.randomUUID(),
            organisasjonsnummer = "123456789",
            navn = "Ole",
            beskrivelse = null,
            telefon = null,
            epost = "ole@bedrift.no",
        ),
        startDato = avtale1.startDato,
        sluttDato = avtale1.sluttDato,
        avtaletype = Avtaletype.Avtale,
        avtalestatus = Avtalestatus.Aktiv,
        prisbetingelser = null,
        administratorer = emptyList(),
        url = null,
        antallPlasser = null,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        updatedAt = avtale1.updatedAt,
        kontorstruktur = listOf(
            Kontorstruktur(
                region = EmbeddedNavEnhet(
                    navn = "NAV Mockdata",
                    enhetsnummer = "0100",
                    type = NavEnhetType.FYLKE,
                    overordnetEnhet = null,
                ),
                kontorer = listOf(
                    EmbeddedNavEnhet(
                        navn = "NAV Mockkontor",
                        enhetsnummer = "0101",
                        type = NavEnhetType.LOKAL,
                        overordnetEnhet = "0100",
                    ),
                ),
            ),
        ),
    )

    val avtaleAdminDto = AvtaleAdminDto(
        id = avtale1.id,
        tiltakstype = AvtaleAdminDto.Tiltakstype(
            id = TiltakstypeFixtures.AFT.id,
            navn = TiltakstypeFixtures.AFT.navn,
            arenaKode = TiltakstypeFixtures.AFT.tiltakskode,
        ),
        navn = avtale1.navn,
        avtalenummer = avtale1.navn,
        leverandor = AvtaleAdminDto.Leverandor(organisasjonsnummer = "123456789", navn = "Bedrift", slettet = false),
        leverandorUnderenheter = emptyList(),
        leverandorKontaktperson = VirksomhetKontaktperson(
            id = UUID.randomUUID(),
            organisasjonsnummer = "123456789",
            navn = "Ole",
            beskrivelse = null,
            telefon = null,
            epost = "ole@bedrift.no",
        ),
        startDato = avtale1.startDato,
        sluttDato = avtale1.sluttDato,
        avtaletype = Avtaletype.Avtale,
        avtalestatus = Avtalestatus.Aktiv,
        prisbetingelser = null,
        administratorer = emptyList(),
        url = null,
        antallPlasser = null,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        updatedAt = avtale1.updatedAt,
        kontorstruktur = listOf(
            Kontorstruktur(
                region = EmbeddedNavEnhet(
                    navn = "NAV Mockdata",
                    enhetsnummer = "0100",
                    type = NavEnhetType.FYLKE,
                    overordnetEnhet = null,
                ),
                kontorer = listOf(
                    EmbeddedNavEnhet(
                        navn = "NAV Mockkontor",
                        enhetsnummer = "0101",
                        type = NavEnhetType.LOKAL,
                        overordnetEnhet = "0100",
                    ),
                ),
            ),
        ),
    )
}
