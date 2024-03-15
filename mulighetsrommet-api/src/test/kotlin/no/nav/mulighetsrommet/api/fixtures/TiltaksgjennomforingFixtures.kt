package no.nav.mulighetsrommet.api.fixtures

import no.nav.mulighetsrommet.api.domain.dbo.TiltaksgjennomforingDbo
import no.nav.mulighetsrommet.api.routes.v1.EstimertVentetid
import no.nav.mulighetsrommet.api.routes.v1.TiltaksgjennomforingRequest
import no.nav.mulighetsrommet.domain.dbo.ArenaTiltaksgjennomforingDbo
import no.nav.mulighetsrommet.domain.dbo.Avslutningsstatus
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingOppstartstype
import no.nav.mulighetsrommet.domain.dto.NavIdent
import java.time.LocalDate
import java.util.*

object TiltaksgjennomforingFixtures {
    val ArenaOppfolging1 = ArenaTiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Oppfølging 1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        tiltaksnummer = "2023#1",
        arrangorOrganisasjonsnummer = "976663934",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        arenaAnsvarligEnhet = "2990",
        avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
        apentForInnsok = true,
        antallPlasser = null,
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        avtaleId = AvtaleFixtures.oppfolging.id,
        deltidsprosent = 100.0,
    )

    val Oppfolging1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Oppfølging 1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = AvtaleFixtures.oppfolging.startDato.plusDays(1),
        sluttDato = AvtaleFixtures.oppfolging.startDato.plusMonths(3),
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = listOf(NavIdent("DD1")),
        navRegion = "0400",
        navEnheter = listOf("0502"),
        oppstart = TiltaksgjennomforingOppstartstype.LOPENDE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.oppfolging.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    val Oppfolging1Request = TiltaksgjennomforingRequest(
        id = Oppfolging1.id,
        navn = Oppfolging1.navn,
        tiltakstypeId = Oppfolging1.tiltakstypeId,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = Oppfolging1.startDato,
        sluttDato = Oppfolging1.sluttDato,
        antallPlasser = Oppfolging1.antallPlasser,
        administratorer = listOf(NavIdent("DD1")),
        navRegion = "2990",
        navEnheter = listOf("2990"),
        oppstart = Oppfolging1.oppstart,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = Oppfolging1.stedForGjennomforing,
        avtaleId = Oppfolging1.avtaleId,
        apentForInnsok = true,
        faneinnhold = Oppfolging1.faneinnhold,
        beskrivelse = Oppfolging1.beskrivelse,
        deltidsprosent = 100.0,
        estimertVentetid = EstimertVentetid(
            verdi = 3,
            enhet = "dag",
        ),
    )

    val Oppfolging2 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Oppfølging 2",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet2.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = emptyList(),
        navRegion = "2990",
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.oppfolging.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    // TODO slett denne
    val Arbeidstrening1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Arbeidstrening 1",
        tiltakstypeId = TiltakstypeFixtures.Arbeidstrening.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet2.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = emptyList(),
        navRegion = "2990",
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.oppfolging.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    val VTA1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "VTA 1",
        tiltakstypeId = TiltakstypeFixtures.VTA.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = null,
        apentForInnsok = true,
        antallPlasser = 12,
        navRegion = "0400",
        navEnheter = listOf("0502"),
        administratorer = listOf(NavIdent("DD1")),
        oppstart = TiltaksgjennomforingOppstartstype.LOPENDE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.VTA.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    val AFT1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "AFT 1",
        tiltakstypeId = TiltakstypeFixtures.AFT.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = null,
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = listOf(NavIdent("DD1")),
        navRegion = "0400",
        navEnheter = listOf("0502"),
        oppstart = TiltaksgjennomforingOppstartstype.LOPENDE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.AFT.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    val Jobbklubb1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Jobbklubb 1",
        tiltakstypeId = TiltakstypeFixtures.Jobbklubb.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = emptyList(),
        navRegion = "2990",
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.oppfolging.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )

    val GruppeAmo1 = TiltaksgjennomforingDbo(
        id = UUID.randomUUID(),
        navn = "Gruppe Amo 1",
        tiltakstypeId = TiltakstypeFixtures.GRUPPE_AMO.id,
        arrangorVirksomhetId = VirksomhetFixtures.underenhet1.id,
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        apentForInnsok = true,
        antallPlasser = 12,
        administratorer = listOf(NavIdent("DD1")),
        navRegion = "0400",
        navEnheter = listOf("0502"),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersoner = emptyList(),
        stedForGjennomforing = "Oslo",
        avtaleId = AvtaleFixtures.gruppeAmo.id,
        faneinnhold = null,
        beskrivelse = null,
        deltidsprosent = 100.0,
        estimertVentetidVerdi = 3,
        estimertVentetidEnhet = "dag",
    )
}
