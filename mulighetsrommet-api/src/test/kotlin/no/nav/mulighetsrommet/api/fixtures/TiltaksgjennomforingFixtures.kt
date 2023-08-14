package no.nav.mulighetsrommet.api.fixtures

import no.nav.mulighetsrommet.api.domain.dbo.TiltaksgjennomforingDbo
import no.nav.mulighetsrommet.api.fixtures.AvtaleFixtures.avtale1Id
import no.nav.mulighetsrommet.api.routes.v1.TiltaksgjennomforingRequest
import no.nav.mulighetsrommet.domain.constants.ArenaMigrering
import no.nav.mulighetsrommet.domain.dbo.ArenaTiltaksgjennomforingDbo
import no.nav.mulighetsrommet.domain.dbo.Avslutningsstatus
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingOppstartstype
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingTilgjengelighetsstatus
import java.time.LocalDate
import java.util.*

object TiltaksgjennomforingFixtures {
    val ArenaOppfolging1 = ArenaTiltaksgjennomforingDbo(
        id = UUID.fromString("21ecff07-18e4-4dba-ada3-0ee7cab5892e"),
        navn = "Oppfølging 1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        tiltaksnummer = "2023#1",
        arrangorOrganisasjonsnummer = "976663934",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        arenaAnsvarligEnhet = "2990",
        avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
        tilgjengelighet = TiltaksgjennomforingTilgjengelighetsstatus.LEDIG,
        antallPlasser = null,
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        avtaleId = avtale1Id,
    )

    val Oppfolging1 = TiltaksgjennomforingDbo(
        id = UUID.fromString("20ecff07-18e4-4dba-ada3-0ee7cab5892e"),
        navn = "Oppfølging 1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        tiltaksnummer = "2023#1",
        arrangorOrganisasjonsnummer = "976663934",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        arenaAnsvarligEnhet = "2990",
        avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
        tilgjengelighet = TiltaksgjennomforingTilgjengelighetsstatus.LEDIG,
        antallPlasser = 12,
        ansvarlige = emptyList(),
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersonId = null,
        stengtFra = null,
        stengtTil = null,
        lokasjonArrangor = "Oslo",
        estimertVentetid = null,
        avtaleId = avtale1Id,
    )

    fun oppfolging1Request(avtaleId: UUID) = TiltaksgjennomforingRequest(
        id = UUID.fromString("20ecff07-18e4-4dba-ada3-0ee7cab5892e"),
        navn = "Oppfølging 1",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        tiltaksnummer = "2023#1",
        arrangorOrganisasjonsnummer = "976663934",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        antallPlasser = 1,
        ansvarlig = "DD1",
        navEnheter = listOf("2990"),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersonId = null,
        stengtFra = null,
        stengtTil = null,
        lokasjonArrangor = "Oslo",
        estimertVentetid = null,
        avtaleId = avtaleId,
        apenForInnsok = true,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
    )

    val Oppfolging2 = TiltaksgjennomforingDbo(
        id = UUID.fromString("170e298a-3431-4959-94ba-d717e640f4a5"),
        navn = "Oppfølging 2",
        tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
        tiltaksnummer = "2023#2",
        arrangorOrganisasjonsnummer = "111111111",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        arenaAnsvarligEnhet = "2990",
        avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
        tilgjengelighet = TiltaksgjennomforingTilgjengelighetsstatus.LEDIG,
        antallPlasser = 12,
        ansvarlige = emptyList(),
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersonId = null,
        stengtFra = null,
        stengtTil = null,
        lokasjonArrangor = "Oslo",
        estimertVentetid = null,
        avtaleId = avtale1Id,
    )

    val Arbeidstrening1 = TiltaksgjennomforingDbo(
        id = UUID.fromString("baae02dc-28c8-4382-be66-6b185adcdd08"),
        navn = "Arbeidstrening 1",
        tiltakstypeId = TiltakstypeFixtures.Arbeidstrening.id,
        tiltaksnummer = "2023#3",
        arrangorOrganisasjonsnummer = "222222222",
        startDato = LocalDate.of(2023, 1, 1),
        sluttDato = LocalDate.of(2023, 2, 1),
        arenaAnsvarligEnhet = "2990",
        avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
        tilgjengelighet = TiltaksgjennomforingTilgjengelighetsstatus.LEDIG,
        antallPlasser = 12,
        ansvarlige = emptyList(),
        navEnheter = emptyList(),
        oppstart = TiltaksgjennomforingOppstartstype.FELLES,
        opphav = ArenaMigrering.Opphav.MR_ADMIN_FLATE,
        kontaktpersoner = emptyList(),
        arrangorKontaktpersonId = null,
        stengtFra = null,
        stengtTil = null,
        lokasjonArrangor = "Oslo",
        estimertVentetid = null,
        avtaleId = avtale1Id,
    )
}
