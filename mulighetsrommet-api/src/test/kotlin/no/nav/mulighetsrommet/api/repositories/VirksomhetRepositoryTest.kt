package no.nav.mulighetsrommet.api.repositories

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotliquery.queryOf
import no.nav.mulighetsrommet.api.createDatabaseTestConfig
import no.nav.mulighetsrommet.api.domain.dto.ArrangorKontaktperson
import no.nav.mulighetsrommet.api.domain.dto.BrregVirksomhetDto
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.api.fixtures.*
import no.nav.mulighetsrommet.api.utils.VirksomhetTil
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener
import no.nav.mulighetsrommet.database.kotest.extensions.truncateAll
import java.time.LocalDate
import java.util.*

class VirksomhetRepositoryTest : FunSpec({
    val database = extension(FlywayDatabaseTestListener(createDatabaseTestConfig()))

    afterEach {
        database.db.truncateAll()
    }

    context("crud") {
        test("søk og filtrering på virksomheter") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val overordnet = VirksomhetDto(
                id = UUID.randomUUID(),
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsert(overordnet)

            val underenhet1 = VirksomhetDto(
                id = UUID.randomUUID(),
                organisasjonsnummer = "880907522",
                overordnetEnhet = overordnet.organisasjonsnummer,
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsert(underenhet1)

            val underenhet2 = VirksomhetDto(
                id = UUID.randomUUID(),
                organisasjonsnummer = "912704327",
                overordnetEnhet = overordnet.organisasjonsnummer,
                navn = "REMA 1000 NORGE AS REGION VESTRE ØSTLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsert(underenhet2)

            val utenlandsk = VirksomhetDto(
                id = UUID.randomUUID(),
                organisasjonsnummer = "100000001",
                navn = "X - Utenlandsk virksomhet",
                postnummer = null,
                poststed = null,
            )
            virksomhetRepository.upsert(utenlandsk)
            queryOf("update arrangor set er_utenlandsk_virksomhet = true where organisasjonsnummer = '${utenlandsk.organisasjonsnummer}'")
                .asExecute
                .let { database.db.run(it) }

            virksomhetRepository.getAll(utenlandsk = true) shouldContainExactlyInAnyOrder listOf(utenlandsk)
            virksomhetRepository.getAll(utenlandsk = false) shouldContainExactlyInAnyOrder listOf(
                overordnet,
                underenhet1,
                underenhet2,
            )

            virksomhetRepository.getAll(sok = "utenlandsk") shouldContainExactlyInAnyOrder listOf(utenlandsk)
            virksomhetRepository.getAll(sok = "østland") shouldContainExactlyInAnyOrder listOf(underenhet2)

            virksomhetRepository.getAll(overordnetEnhetOrgnr = overordnet.organisasjonsnummer) shouldContainExactlyInAnyOrder listOf(
                underenhet1,
                underenhet2,
            )
            virksomhetRepository.getAll(overordnetEnhetOrgnr = underenhet1.organisasjonsnummer).shouldBeEmpty()
        }

        test("Upsert underenhet etter overenhet") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val underenhet1 = BrregVirksomhetDto(
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = BrregVirksomhetDto(
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = listOf(),
                postnummer = "5174",
                poststed = "Mathopen",
            )

            virksomhetRepository.upsert(overordnet)
            virksomhetRepository.upsert(underenhet1)

            virksomhetRepository.get(underenhet1.organisasjonsnummer).should {
                it.shouldNotBeNull()
                it.organisasjonsnummer shouldBe underenhet1.organisasjonsnummer
            }
            virksomhetRepository.get(overordnet.organisasjonsnummer).should {
                it.shouldNotBeNull()
                it.underenheter.shouldNotBeNull().shouldHaveSize(1).first().should { e ->
                    e.navn shouldBe underenhet1.navn
                    e.organisasjonsnummer shouldBe underenhet1.organisasjonsnummer
                }
            }
        }

        test("Upsert slettet enhet") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val slettetDato = LocalDate.of(2024, 1, 1)

            val underenhet1 = VirksomhetDto(
                id = UUID.randomUUID(),
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                slettetDato = slettetDato,
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = VirksomhetDto(
                id = UUID.randomUUID(),
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            virksomhetRepository.upsert(overordnet)
            virksomhetRepository.upsert(underenhet1)

            virksomhetRepository.get(overordnet.organisasjonsnummer).should {
                it.shouldNotBeNull()
                it.slettetDato shouldBe null
            }
            virksomhetRepository.get(underenhet1.organisasjonsnummer).should {
                it.shouldNotBeNull()
                it.slettetDato shouldBe slettetDato
            }
            virksomhetRepository.getAll(slettet = true) shouldContainExactlyInAnyOrder listOf(underenhet1)
            virksomhetRepository.getAll(slettet = false) shouldContainExactlyInAnyOrder listOf(overordnet)
        }

        test("Filter på avtale eller gjennomforing") {
            val hovedenhet = VirksomhetFixtures.hovedenhet
            val underenhet = VirksomhetFixtures.underenhet1

            MulighetsrommetTestDomain(
                virksomheter = listOf(hovedenhet, underenhet),
                tiltakstyper = listOf(TiltakstypeFixtures.Oppfolging),
                avtaler = listOf(AvtaleFixtures.oppfolging),
                gjennomforinger = listOf(TiltaksgjennomforingFixtures.Oppfolging1),
            ).initialize(database.db)

            val virksomhetRepository = VirksomhetRepository(database.db)

            virksomhetRepository.getAll() shouldContainExactlyInAnyOrder listOf(hovedenhet, underenhet)
            virksomhetRepository.getAll(til = VirksomhetTil.AVTALE).should {
                it.size shouldBe 1
                it[0] shouldBe hovedenhet
            }
            virksomhetRepository.getAll(til = VirksomhetTil.TILTAKSGJENNOMFORING).should {
                it.size shouldBe 1
                it[0] shouldBe underenhet
            }
        }
    }

    context("virksomhet_kontaktperson") {
        test("crud") {
            val virksomhetRepository = VirksomhetRepository(database.db)
            val virksomhetId = UUID.randomUUID()
            val virksomhet = VirksomhetDto(
                id = virksomhetId,
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = null,
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsert(virksomhet)

            val kontaktperson = ArrangorKontaktperson(
                id = UUID.randomUUID(),
                arrangorId = virksomhetId,
                navn = "Fredrik",
                telefon = "322232323",
                epost = "fredrik@gmail.com",
                beskrivelse = null,
            )
            val kontaktperson2 = ArrangorKontaktperson(
                id = UUID.randomUUID(),
                arrangorId = virksomhetId,
                navn = "Trond",
                telefon = "232232323",
                epost = "trond@gmail.com",
                beskrivelse = "Adm. dir.",
            )
            virksomhetRepository.upsertKontaktperson(kontaktperson)
            virksomhetRepository.upsertKontaktperson(kontaktperson2)

            virksomhetRepository.getKontaktpersoner(virksomhetId) shouldContainExactlyInAnyOrder listOf(
                kontaktperson,
                kontaktperson2,
            )
        }
    }
})
