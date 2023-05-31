package no.nav.mulighetsrommet.api.repositories

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet.api.createDatabaseTestConfig
import no.nav.mulighetsrommet.api.domain.dbo.OverordnetEnhetDbo
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener

class VirksomhetRepositoryTest : FunSpec({
    val database = extension(FlywayDatabaseTestListener(createDatabaseTestConfig()))

    beforeEach {
        database.db.clean()
        database.db.migrate()
    }

    context("crud") {
        test("Upsert virksomhet med underenheter") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val underenhet1 = VirksomhetDto(
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            val underenhet2 = VirksomhetDto(
                organisasjonsnummer = "912704327",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION VESTRE ØSTLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            val underenhet3 = VirksomhetDto(
                organisasjonsnummer = "912704394",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORD",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = OverordnetEnhetDbo(
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = listOf(underenhet1, underenhet2, underenhet3),
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsertOverordnetEnhet(overordnet).shouldBeRight()

            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it!!.navn shouldBe "REMA 1000 AS"
                it.underenheter!! shouldHaveSize 3
                it.underenheter!! shouldContainAll listOf(underenhet1, underenhet2, underenhet3)
            }

            virksomhetRepository.upsertOverordnetEnhet(overordnet.copy(underenheter = listOf(underenhet1)))
                .shouldBeRight()
            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it!!.underenheter!! shouldHaveSize 1
                it.underenheter!! shouldContain underenhet1
            }
        }

        test("Upsert virksomhet med underenheter oppdaterer korrekt data ved conflict på organisasjonsnummer") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val underenhet1 = VirksomhetDto(
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            val underenhet2 = VirksomhetDto(
                organisasjonsnummer = "912704327",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION VESTRE ØSTLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )
            val underenhet3 = VirksomhetDto(
                organisasjonsnummer = "912704394",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORD",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = OverordnetEnhetDbo(
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = listOf(underenhet1, underenhet2, underenhet3),
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsertOverordnetEnhet(overordnet).shouldBeRight()
            virksomhetRepository.upsertOverordnetEnhet(overordnet.copy(postnummer = "9988", poststed = "Olsenåsen", navn = "Stopp konflikten")).shouldBeRight()

            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it!!.navn shouldBe "Stopp konflikten"
                it.postnummer shouldBe "9988"
                it.poststed shouldBe "Olsenåsen"
                it.underenheter!! shouldHaveSize 3
                it.underenheter!! shouldContainAll listOf(underenhet1, underenhet2, underenhet3)
            }

            virksomhetRepository.upsertOverordnetEnhet(overordnet.copy(underenheter = listOf(underenhet1)))
                .shouldBeRight()
            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it!!.underenheter!! shouldHaveSize 1
                it.underenheter!! shouldContain underenhet1
            }
        }

        test("Upsert underenhet etter overenhet") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val underenhet1 = VirksomhetDto(
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = VirksomhetDto(
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = listOf(), // Tom først
                postnummer = "5174",
                poststed = "Mathopen",
            )

            virksomhetRepository.upsert(overordnet).shouldBeRight()
            virksomhetRepository.upsert(underenhet1).shouldBeRight()

            virksomhetRepository.get(underenhet1.organisasjonsnummer).shouldBeRight().should {
                it!!.organisasjonsnummer shouldBe underenhet1.organisasjonsnummer
            }
            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it!!.underenheter shouldContainExactly listOf(underenhet1)
            }
        }

        test("Delete overordnet cascader") {
            val virksomhetRepository = VirksomhetRepository(database.db)

            val underenhet1 = VirksomhetDto(
                organisasjonsnummer = "880907522",
                overordnetEnhet = "982254604",
                navn = "REMA 1000 NORGE AS REGION NORDLAND",
                postnummer = "5174",
                poststed = "Mathopen",
            )

            val overordnet = OverordnetEnhetDbo(
                navn = "REMA 1000 AS",
                organisasjonsnummer = "982254604",
                underenheter = listOf(underenhet1),
                postnummer = "5174",
                poststed = "Mathopen",
            )
            virksomhetRepository.upsertOverordnetEnhet(overordnet).shouldBeRight()

            virksomhetRepository.get(underenhet1.organisasjonsnummer).shouldBeRight().should {
                it!!.organisasjonsnummer shouldBe underenhet1.organisasjonsnummer
            }

            virksomhetRepository.delete(overordnet.organisasjonsnummer).shouldBeRight()
            virksomhetRepository.get(underenhet1.organisasjonsnummer).shouldBeRight().should {
                it shouldBe null
            }
            virksomhetRepository.get(overordnet.organisasjonsnummer).shouldBeRight().should {
                it shouldBe null
            }
        }
    }
})