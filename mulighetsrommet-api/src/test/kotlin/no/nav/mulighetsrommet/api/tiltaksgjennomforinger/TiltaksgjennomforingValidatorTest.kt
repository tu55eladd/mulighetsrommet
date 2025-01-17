package no.nav.mulighetsrommet.api.tiltaksgjennomforinger

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet.api.clients.norg2.Norg2Type
import no.nav.mulighetsrommet.api.createDatabaseTestConfig
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetDbo
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetStatus
import no.nav.mulighetsrommet.api.fixtures.*
import no.nav.mulighetsrommet.api.repositories.ArrangorRepository
import no.nav.mulighetsrommet.api.repositories.AvtaleRepository
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.repositories.TiltakstypeRepository
import no.nav.mulighetsrommet.api.responses.ValidationError
import no.nav.mulighetsrommet.api.services.TiltakstypeService
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener
import no.nav.mulighetsrommet.domain.Tiltakskode
import no.nav.mulighetsrommet.domain.constants.ArenaMigrering
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingOppstartstype
import no.nav.mulighetsrommet.domain.dto.AvbruttAarsak
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class TiltaksgjennomforingValidatorTest : FunSpec({
    val database = extension(FlywayDatabaseTestListener(createDatabaseTestConfig()))

    val avtaleStartDato = LocalDate.now()
    val avtaleSluttDato = LocalDate.now().plusMonths(1)
    val avtale = AvtaleFixtures.oppfolging.copy(
        id = UUID.randomUUID(),
        startDato = avtaleStartDato,
        sluttDato = avtaleSluttDato,
        arrangorId = ArrangorFixtures.hovedenhet.id,
        arrangorUnderenheter = listOf(ArrangorFixtures.underenhet1.id),
        navEnheter = listOf("0400", "0502"),
    )

    val gjennomforing = TiltaksgjennomforingFixtures.Oppfolging1.copy(
        avtaleId = avtale.id,
        startDato = avtaleStartDato,
        sluttDato = avtaleSluttDato,
        navRegion = "0400",
        navEnheter = listOf("0502"),
        arrangorId = ArrangorFixtures.underenhet1.id,
        administratorer = listOf(NavAnsattFixture.ansatt1.navIdent),
    )

    val domain = MulighetsrommetTestDomain(
        enheter = listOf(
            NavEnhetDbo(
                navn = "NAV Oslo",
                enhetsnummer = "0300",
                status = NavEnhetStatus.AKTIV,
                type = Norg2Type.FYLKE,
                overordnetEnhet = null,
            ),
            NavEnhetDbo(
                navn = "NAV Innlandet",
                enhetsnummer = "0400",
                status = NavEnhetStatus.AKTIV,
                type = Norg2Type.FYLKE,
                overordnetEnhet = null,
            ),
            NavEnhetDbo(
                navn = "NAV Gjøvik",
                enhetsnummer = "0502",
                status = NavEnhetStatus.AKTIV,
                type = Norg2Type.LOKAL,
                overordnetEnhet = "0400",
            ),
            NavEnhetDbo(
                navn = "NAV IT",
                enhetsnummer = "2990",
                status = NavEnhetStatus.AKTIV,
                type = Norg2Type.IT,
                overordnetEnhet = null,
            ),
        ),
        arrangorer = listOf(
            ArrangorFixtures.hovedenhet,
            ArrangorFixtures.underenhet1,
            ArrangorFixtures.underenhet2,
        ),
        ansatte = listOf(NavAnsattFixture.ansatt1, NavAnsattFixture.ansatt2),
        tiltakstyper = listOf(
            TiltakstypeFixtures.VTA,
            TiltakstypeFixtures.AFT,
            TiltakstypeFixtures.Jobbklubb,
            TiltakstypeFixtures.GruppeAmo,
            TiltakstypeFixtures.Oppfolging,
        ),
        avtaler = listOf(
            avtale,
            AvtaleFixtures.oppfolgingMedAvtale,
            AvtaleFixtures.oppfolging,
            AvtaleFixtures.gruppeAmo,
            AvtaleFixtures.jobbklubb,
            AvtaleFixtures.VTA,
            AvtaleFixtures.AFT,
        ),
    )

    lateinit var tiltakstyper: TiltakstypeService
    lateinit var avtaler: AvtaleRepository
    lateinit var tiltaksgjennomforinger: TiltaksgjennomforingRepository
    lateinit var arrangorer: ArrangorRepository

    beforeEach {
        domain.initialize(database.db)

        tiltakstyper = TiltakstypeService(
            TiltakstypeRepository(database.db),
            listOf(Tiltakskode.OPPFOLGING),

        )
        avtaler = AvtaleRepository(database.db)
        tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
        arrangorer = ArrangorRepository(database.db)
    }

    test("should fail when tiltakstype is not enabled") {
        tiltakstyper = TiltakstypeService(TiltakstypeRepository(database.db), emptyList())
        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        validator.validate(gjennomforing, null).shouldBeLeft().shouldContainExactlyInAnyOrder(
            ValidationError(
                "avtaleId",
                "Opprettelse av tiltaksgjennomføring for tiltakstype: 'Oppfølging' er ikke skrudd på enda.",
            ),
        )
    }

    test("should fail when avtale does not exist") {
        val unknownAvtaleId = UUID.randomUUID()

        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        val dbo = gjennomforing.copy(avtaleId = unknownAvtaleId)

        validator.validate(dbo, null).shouldBeLeft().shouldContainExactlyInAnyOrder(
            ValidationError("avtaleId", "Avtalen finnes ikke"),
        )
    }

    test("should fail when tiltakstype does not match with avtale") {
        avtaler.upsert(avtale.copy(tiltakstypeId = TiltakstypeFixtures.AFT.id))

        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        validator.validate(gjennomforing, null).shouldBeLeft().shouldContainExactlyInAnyOrder(
            ValidationError("tiltakstypeId", "Tiltakstypen må være den samme som for avtalen"),
        )
    }

    test("should fail when tiltakstype does not support change of oppstartstype") {
        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        validator.validate(gjennomforing.copy(oppstart = TiltaksgjennomforingOppstartstype.FELLES), null)
            .shouldBeLeft()
            .shouldContainExactlyInAnyOrder(ValidationError("oppstart", "Tiltaket må ha løpende oppstartstype"))
    }

    test("kan ikke opprette på ikke Aktiv avtale") {
        val id = UUID.randomUUID()
        avtaler.upsert(avtale.copy(id = id))

        avtaler.avbryt(id, LocalDateTime.now(), AvbruttAarsak.BudsjettHensyn)

        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)
        val dbo = gjennomforing.copy(avtaleId = id)

        validator.validate(dbo, null).shouldBeLeft(
            listOf(ValidationError("avtaleId", "Avtalen må være aktiv for å kunne opprette tiltak")),
        )

        val id2 = UUID.randomUUID()
        avtaler.upsert(avtale.copy(id = id2, sluttDato = LocalDate.now().minusDays(1)))

        val dbo2 = gjennomforing.copy(avtaleId = id2)

        validator.validate(dbo2, null).shouldBeLeft(
            listOf(ValidationError("avtaleId", "Avtalen må være aktiv for å kunne opprette tiltak")),
        )
    }

    test("kan ikke opprette før Avtale startDato") {
        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)
        val dbo = gjennomforing.copy(
            startDato = avtale.startDato.minusDays(1),
        )

        validator.validate(dbo, null).shouldBeLeft(
            listOf(ValidationError("startDato", "Du må legge inn en startdato som er etter avtalens startdato")),
        )
    }

    test("skal returnere en ny verdi for 'tilgjengelig for arrangør'-dato når datoen er utenfor gyldig tidsrom") {
        val startDato = LocalDate.now().plusMonths(1)
        val dbo = gjennomforing.copy(startDato = startDato)
        tiltaksgjennomforinger.upsert(dbo)

        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        val beforeAllowedDato = startDato.minusMonths(3)
        validator.validate(gjennomforing.copy(tilgjengeligForArrangorFraOgMedDato = beforeAllowedDato), null)
            .shouldBeRight().should {
                it.tilgjengeligForArrangorFraOgMedDato.shouldBeNull()
            }

        val afterStartDato = startDato.plusDays(1)
        validator.validate(dbo.copy(tilgjengeligForArrangorFraOgMedDato = afterStartDato), null)
            .shouldBeRight().should {
                it.tilgjengeligForArrangorFraOgMedDato.shouldBeNull()
            }

        val beforeStartDato = startDato.minusDays(1)
        validator.validate(dbo.copy(tilgjengeligForArrangorFraOgMedDato = beforeStartDato), null)
            .shouldBeRight().should {
                it.tilgjengeligForArrangorFraOgMedDato shouldBe beforeStartDato
            }
    }

    test("sluttDato er påkrevd hvis ikke forhåndsgodkjent avtale") {
        val validator = TiltaksgjennomforingValidator(
            TiltakstypeService(TiltakstypeRepository(database.db), Tiltakskode.entries),
            avtaler,
            arrangorer,
        )
        val forhaandsgodkjent = TiltaksgjennomforingFixtures.AFT1.copy(sluttDato = null)
        val rammeAvtale = TiltaksgjennomforingFixtures.Oppfolging1.copy(sluttDato = null)
        val vanligAvtale = TiltaksgjennomforingFixtures.Oppfolging1.copy(
            sluttDato = null,
            avtaleId = AvtaleFixtures.oppfolgingMedAvtale.id,
        )
        val offentligOffentlig = TiltaksgjennomforingFixtures.GruppeAmo1.copy(sluttDato = null)

        validator.validate(forhaandsgodkjent, null).shouldBeRight()
        validator.validate(rammeAvtale, null).shouldBeLeft(
            listOf(ValidationError("sluttDato", "Du må legge inn sluttdato for gjennomføringen")),
        )
        validator.validate(vanligAvtale, null).shouldBeLeft(
            listOf(ValidationError("sluttDato", "Du må legge inn sluttdato for gjennomføringen")),
        )
        validator.validate(offentligOffentlig, null).shouldBeLeft(
            listOf(ValidationError("sluttDato", "Du må legge inn sluttdato for gjennomføringen")),
        )
    }

    test("kurstittel er påkrevd hvis kurstiltak") {
        val validator = TiltaksgjennomforingValidator(
            TiltakstypeService(TiltakstypeRepository(database.db), Tiltakskode.entries),
            avtaler,
            arrangorer,
        )
        val jobbklubb = TiltaksgjennomforingFixtures.Jobbklubb1.copy(faneinnhold = null)
        val gruppeAmo = TiltaksgjennomforingFixtures.GruppeAmo1.copy(faneinnhold = null)
        val oppfolging = TiltaksgjennomforingFixtures.Oppfolging1

        validator.validate(jobbklubb, null).shouldBeLeft(
            listOf(ValidationError("faneinnhold.kurstittel", "Du må skrive en kurstittel")),
        )
        validator.validate(gruppeAmo, null).shouldBeLeft(
            listOf(ValidationError("faneinnhold.kurstittel", "Du må skrive en kurstittel")),
        )
        validator.validate(oppfolging, null).shouldBeRight()
    }

    test("arrangøren må være aktiv i Brreg") {
        arrangorer.upsert(ArrangorFixtures.underenhet1.copy(slettetDato = LocalDate.of(2024, 1, 1)))

        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        validator.validate(gjennomforing, null).shouldBeLeft().shouldContainExactlyInAnyOrder(
            ValidationError(
                "arrangorId",
                "Arrangøren Underenhet 1 AS er slettet i Brønnøysundregistrene. Gjennomføringer kan ikke opprettes for slettede bedrifter.",
            ),
        )
    }

    test("should validate fields in the gjennomføring and fields related to the avtale") {
        val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

        forAll(
            row(
                gjennomforing.copy(
                    startDato = avtaleStartDato.minusDays(1),
                    sluttDato = avtaleStartDato,
                ),
                listOf(ValidationError("startDato", "Du må legge inn en startdato som er etter avtalens startdato")),
            ),
            row(
                gjennomforing.copy(
                    startDato = avtaleSluttDato,
                    sluttDato = avtaleStartDato,
                ),
                listOf(ValidationError("startDato", "Startdato må være før sluttdato")),
            ),
            row(
                gjennomforing.copy(antallPlasser = 0),
                listOf(ValidationError("antallPlasser", "Du må legge inn antall plasser større enn 0")),
            ),
            row(
                gjennomforing.copy(navEnheter = listOf("0401")),
                listOf(ValidationError("navEnheter", "NAV-enhet 0401 mangler i avtalen")),
            ),
            row(
                gjennomforing.copy(arrangorId = ArrangorFixtures.underenhet2.id),
                listOf(ValidationError("arrangorId", "Du må velge en arrangør for avtalen")),
            ),
        ) { input, error ->
            validator.validate(input, null).shouldBeLeft(error)
        }
    }

    context("when gjennomføring already exists") {
        beforeEach {
            tiltaksgjennomforinger.upsert(gjennomforing.copy(administratorer = listOf()))
        }

        afterEach {
            tiltaksgjennomforinger.delete(gjennomforing.id)
        }

        test("skal ikke kunne endre felter med opphav fra Arena når vi ikke har tatt eierskap til tiltakstypen") {
            val gjennomforingMedEndringer = gjennomforing.copy(
                navn = "Nytt navn",
                tiltakstypeId = TiltakstypeFixtures.Oppfolging.id,
                arrangorId = ArrangorFixtures.underenhet2.id,
                startDato = gjennomforing.startDato.plusDays(1),
                sluttDato = gjennomforing.sluttDato?.plusDays(1),
                apentForInnsok = false,
                antallPlasser = 1000,
                deltidsprosent = 1.5,
            )

            tiltaksgjennomforinger.setOpphav(gjennomforing.id, ArenaMigrering.Opphav.ARENA)

            val validator = TiltaksgjennomforingValidator(
                TiltakstypeService(TiltakstypeRepository(database.db), listOf()),
                avtaler,
                arrangorer,
            )

            val current = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforingMedEndringer, current).shouldBeLeft().shouldContainExactlyInAnyOrder(
                ValidationError("navn", "Navn kan ikke endres utenfor Arena"),
                ValidationError("startDato", "Startdato kan ikke endres utenfor Arena"),
                ValidationError("sluttDato", "Sluttdato kan ikke endres utenfor Arena"),
                ValidationError("apentForInnsok", "Åpent for innsøk kan ikke endres utenfor Arena"),
                ValidationError("antallPlasser", "Antall plasser kan ikke endres utenfor Arena"),
                ValidationError("deltidsprosent", "Deltidsprosent kan ikke endres utenfor Arena"),
                ValidationError("arrangorId", "Du kan ikke endre arrangør når gjennomføringen er aktiv"),
                ValidationError("arrangorId", "Du må velge en arrangør for avtalen"),
            )
        }

        test("Skal godta endringer for antall plasser selv om gjennomføringen er aktiv") {
            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)
            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforing.copy(antallPlasser = 15), previous).shouldBeRight()
        }

        test("Skal godta endringer for startdato selv om gjennomføringen er aktiv, men startdato skal ikke kunne settes til før avtaledatoen") {
            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)
            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforing.copy(startDato = LocalDate.now().plusDays(5)), previous).shouldBeRight()
            validator.validate(gjennomforing.copy(startDato = avtaleStartDato.minusDays(1)), previous).shouldBeLeft(
                listOf(
                    ValidationError("startDato", "Du må legge inn en startdato som er etter avtalens startdato"),
                ),
            )
        }

        test("Skal godta endringer for sluttdato frem i tid selv om gjennomføringen er aktiv") {
            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)
            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            avtaler.upsert(avtale.copy(startDato = LocalDate.now().minusDays(3)))
            validator.validate(gjennomforing.copy(sluttDato = avtaleSluttDato.plusDays(5)), previous).shouldBeRight()
            validator.validate(
                gjennomforing.copy(
                    startDato = LocalDate.now().minusDays(2),
                    sluttDato = LocalDate.now().minusDays(1),
                ),
                previous,
            ).shouldBeLeft(
                listOf(
                    ValidationError(
                        "sluttDato",
                        "Du kan ikke sette en sluttdato bakover i tid når gjennomføringen er aktiv",
                    ),
                ),
            )
        }

        test("skal godta endringer selv om avtale er avbrutt") {
            avtaler.avbryt(avtale.id, LocalDateTime.now(), AvbruttAarsak.BudsjettHensyn)

            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforing, previous).shouldBeRight()
        }

        test("should fail when is avbrutt") {
            tiltaksgjennomforinger.avbryt(gjennomforing.id, LocalDateTime.now(), AvbruttAarsak.Feilregistrering)

            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforing, previous).shouldBeLeft().shouldContainExactlyInAnyOrder(
                ValidationError("navn", "Du kan ikke gjøre endringer på en gjennomføring som ikke er aktiv"),
            )
        }

        test("should fail when is avsluttet") {
            tiltaksgjennomforinger.upsert(gjennomforing.copy(sluttDato = LocalDate.now().minusDays(2)))

            val validator = TiltaksgjennomforingValidator(tiltakstyper, avtaler, arrangorer)

            val previous = tiltaksgjennomforinger.get(gjennomforing.id)
            validator.validate(gjennomforing, previous).shouldBeLeft().shouldContainExactlyInAnyOrder(
                ValidationError("navn", "Du kan ikke gjøre endringer på en gjennomføring som ikke er aktiv"),
            )
        }
    }
})
