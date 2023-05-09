package no.nav.mulighetsrommet.api.repositories

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet.api.clients.norg2.Norg2Type
import no.nav.mulighetsrommet.api.createDatabaseTestConfig
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetDbo
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetStatus
import no.nav.mulighetsrommet.api.fixtures.AvtaleFixtures
import no.nav.mulighetsrommet.api.fixtures.TiltaksgjennomforingFixtures
import no.nav.mulighetsrommet.api.fixtures.TiltakstypeFixtures
import no.nav.mulighetsrommet.api.utils.AdminTiltaksgjennomforingFilter
import no.nav.mulighetsrommet.api.utils.DEFAULT_PAGINATION_LIMIT
import no.nav.mulighetsrommet.api.utils.PaginationParams
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener
import no.nav.mulighetsrommet.domain.dbo.*
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingDbo.Tilgjengelighetsstatus
import no.nav.mulighetsrommet.domain.dto.TiltaksgjennomforingAdminDto
import no.nav.mulighetsrommet.domain.dto.Tiltaksgjennomforingsstatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class TiltaksgjennomforingRepositoryTest : FunSpec({
    val database = extension(FlywayDatabaseTestListener(createDatabaseTestConfig()))

    val tiltakstype1 = TiltakstypeFixtures.Arbeidstrening

    val tiltakstype2 = TiltakstypeFixtures.Oppfolging

    val avtaleFixtures = AvtaleFixtures(database)

    val avtale1 = avtaleFixtures.createAvtaleForTiltakstype(tiltakstypeId = tiltakstype1.id)

    val avtale2 = avtaleFixtures.createAvtaleForTiltakstype(tiltakstypeId = tiltakstype2.id)

    val gjennomforing1 = TiltaksgjennomforingFixtures.Arbeidstrening1

    val gjennomforing2 = TiltaksgjennomforingFixtures.Oppfolging1

    beforeAny {
        database.db.clean()
        database.db.migrate()

        val tiltakstyper = TiltakstypeRepository(database.db)
        tiltakstyper.upsert(tiltakstype1)
        tiltakstyper.upsert(tiltakstype2)

        avtaleFixtures.upsertAvtaler(listOf(avtale1, avtale2))
    }

    context("CRUD") {

        test("CRUD") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)

            tiltaksgjennomforinger.upsert(gjennomforing1).shouldBeRight()
            tiltaksgjennomforinger.upsert(gjennomforing2).shouldBeRight()

            tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter())
                .shouldBeRight().second shouldHaveSize 2
            tiltaksgjennomforinger.get(gjennomforing1.id).shouldBeRight() shouldBe TiltaksgjennomforingAdminDto(
                id = gjennomforing1.id,
                tiltakstype = TiltaksgjennomforingAdminDto.Tiltakstype(
                    id = tiltakstype1.id,
                    navn = tiltakstype1.navn,
                    arenaKode = tiltakstype1.tiltakskode,
                ),
                navn = gjennomforing1.navn,
                tiltaksnummer = gjennomforing1.tiltaksnummer,
                virksomhetsnummer = gjennomforing1.virksomhetsnummer,
                startDato = gjennomforing1.startDato,
                sluttDato = gjennomforing1.sluttDato,
                arenaAnsvarligEnhet = gjennomforing1.arenaAnsvarligEnhet,
                status = Tiltaksgjennomforingsstatus.AVSLUTTET,
                tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                antallPlasser = null,
                avtaleId = gjennomforing1.avtaleId,
                ansvarlige = emptyList(),
                enheter = emptyList(),
            )

            tiltaksgjennomforinger.delete(gjennomforing1.id)

            tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter())
                .shouldBeRight().second shouldHaveSize 1
        }

        test("enheter crud") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val enhetRepository = NavEnhetRepository(database.db)
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = "Navn1",
                    enhetsnummer = "1",
                    status = NavEnhetStatus.AKTIV,
                    type = Norg2Type.LOKAL,
                    overordnetEnhet = null,
                ),
            ).shouldBeRight()
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = "Navn2",
                    enhetsnummer = "2",
                    status = NavEnhetStatus.AKTIV,
                    type = Norg2Type.LOKAL,
                    overordnetEnhet = null,
                ),
            ).shouldBeRight()
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = "Navn3",
                    enhetsnummer = "3",
                    status = NavEnhetStatus.AKTIV,
                    type = Norg2Type.LOKAL,
                    overordnetEnhet = null,
                ),
            ).shouldBeRight()

            val gjennomforing = gjennomforing1.copy(enheter = listOf("1", "2"))

            tiltaksgjennomforinger.upsert(gjennomforing).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.enheter.shouldContainExactlyInAnyOrder("1", "2")
            }
            database.assertThat("tiltaksgjennomforing_nav_enhet").hasNumberOfRows(2)

            tiltaksgjennomforinger.upsert(gjennomforing.copy(enheter = listOf("3", "1"))).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.enheter.shouldContainExactlyInAnyOrder("1", "3")
            }
            database.assertThat("tiltaksgjennomforing_nav_enhet").hasNumberOfRows(2)
        }

        test("Oppdater enheter fra Sanity-tiltaksgjennomføringer til database") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val enhetRepository = NavEnhetRepository(database.db)
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = "Navn1",
                    enhetsnummer = "1",
                    status = NavEnhetStatus.AKTIV,
                    type = Norg2Type.LOKAL,
                    overordnetEnhet = null,
                ),
            ).shouldBeRight()
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = "Navn2",
                    enhetsnummer = "2",
                    status = NavEnhetStatus.AKTIV,
                    type = Norg2Type.LOKAL,
                    overordnetEnhet = null,
                ),
            ).shouldBeRight()

            val gjennomforing = gjennomforing1.copy(tiltaksnummer = "2023#1")

            tiltaksgjennomforinger.upsert(gjennomforing).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.enheter.shouldBeEmpty()
            }
            tiltaksgjennomforinger.updateEnheter("1", listOf("1", "2")).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.enheter.shouldContainExactlyInAnyOrder("1", "2")
            }
            database.assertThat("tiltaksgjennomforing_nav_enhet").hasNumberOfRows(2)

            tiltaksgjennomforinger.updateEnheter("1", listOf("2")).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.enheter.shouldContainExactlyInAnyOrder("2")
            }
            database.assertThat("tiltaksgjennomforing_nav_enhet").hasNumberOfRows(1)
        }
    }

    context("Filtrer på avtale") {
        test("Kun gjennomforinger tilhørende avtale blir tatt med") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)

            tiltaksgjennomforinger.upsert(gjennomforing1).shouldBeRight()
            tiltaksgjennomforinger.upsert(gjennomforing2.copy(avtaleId = avtale1.id)).shouldBeRight()
            tiltaksgjennomforinger.upsert(gjennomforing2.copy(id = UUID.randomUUID(), avtaleId = avtale2.id))
                .shouldBeRight()

            val result =
                tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter(avtaleId = avtale1.id))
                    .shouldBeRight().second
            result shouldHaveSize 1
            result.first().id shouldBe gjennomforing2.id
        }
    }

    context("Cutoffdato") {
        test("Gamle tiltaksgjennomføringer blir ikke tatt med") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)

            tiltaksgjennomforinger.upsert(gjennomforing1).shouldBeRight()
            tiltaksgjennomforinger.upsert(
                gjennomforing1.copy(
                    id = UUID.randomUUID(),
                    sluttDato = LocalDate.of(2022, 12, 31),
                ),
            ).shouldBeRight()

            val result =
                tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter()).shouldBeRight().second
            result shouldHaveSize 1
            result.first().id shouldBe gjennomforing1.id
        }

        test("Tiltaksgjennomføringer med sluttdato som er null blir tatt med") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)

            tiltaksgjennomforinger.upsert(gjennomforing1).shouldBeRight()
            tiltaksgjennomforinger.upsert(gjennomforing1.copy(id = UUID.randomUUID(), sluttDato = null)).shouldBeRight()

            tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter())
                .shouldBeRight().second shouldHaveSize 2
        }
    }

    context("TiltaksgjennomforingAnsvarlig") {
        test("Ansvarlige crud") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)

            val ident1 = "N12343"
            val ident2 = "Y12343"
            val gjennomforing = gjennomforing1.copy(ansvarlige = listOf(ident1, ident2))

            tiltaksgjennomforinger.upsert(gjennomforing).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.ansvarlige.shouldContainExactlyInAnyOrder(ident1, ident2)
            }

            database.assertThat("tiltaksgjennomforing_ansvarlig").hasNumberOfRows(2)

            val ident3 = "X12343"
            tiltaksgjennomforinger.upsert(gjennomforing.copy(ansvarlige = listOf(ident3))).shouldBeRight()
            tiltaksgjennomforinger.get(gjennomforing.id).shouldBeRight().should {
                it!!.ansvarlige.shouldContainExactlyInAnyOrder(ident3)
            }

            database.assertThat("tiltaksgjennomforing_ansvarlig").hasNumberOfRows(1)
        }
    }

    context("tilgjengelighetsstatus") {
        val tiltakstyper = TiltakstypeRepository(database.db)
        tiltakstyper.upsert(tiltakstype1)

        val deltakere = DeltakerRepository(database.db)
        val deltaker = DeltakerDbo(
            id = UUID.randomUUID(),
            tiltaksgjennomforingId = gjennomforing1.id,
            status = Deltakerstatus.DELTAR,
            opphav = Deltakeropphav.AMT,
            startDato = null,
            sluttDato = null,
            registrertDato = LocalDateTime.of(2023, 3, 1, 0, 0, 0),
        )

        context("when tilgjengelighet is set to Stengt") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Stengt,
                    ),
                ).shouldBeRight()
            }

            test("should have tilgjengelighet set to Stengt") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Stengt
            }
        }

        context("when avslutningsstatus is set") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        avslutningsstatus = Avslutningsstatus.AVSLUTTET,
                    ),
                ).shouldBeRight()
            }

            test("should have tilgjengelighet set to Stengt") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Stengt
            }
        }

        context("when there are no limits to available seats") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        antallPlasser = null,
                    ),
                ).shouldBeRight()
            }

            test("should have tilgjengelighet set to Ledig") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Ledig
            }
        }

        context("when there are no available seats") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        antallPlasser = 0,
                    ),
                ).shouldBeRight()
            }

            test("should have tilgjengelighet set to Venteliste") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Venteliste
            }
        }

        context("when all available seats are occupied by deltakelser with status DELTAR") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        antallPlasser = 1,
                    ),
                ).shouldBeRight()

                deltakere.upsert(deltaker.copy(status = Deltakerstatus.DELTAR))
            }

            test("should have tilgjengelighet set to Venteliste") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Venteliste
            }
        }

        context("when deltakelser are no longer DELTAR") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            beforeAny {
                tiltaksgjennomforinger.upsert(
                    gjennomforing1.copy(
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        antallPlasser = 1,
                    ),
                ).shouldBeRight()

                deltakere.upsert(deltaker.copy(status = Deltakerstatus.AVSLUTTET))
            }

            test("should have tilgjengelighet set to Ledig") {
                tiltaksgjennomforinger.get(gjennomforing1.id)
                    .shouldBeRight()?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Ledig
            }
        }
    }

    context("Filtrering på tiltaksgjennomforingstatus") {
        val defaultFilter = AdminTiltaksgjennomforingFilter(
            dagensDato = LocalDate.of(2023, 2, 1),
        )

        val tiltaksgjennomforingAktiv = gjennomforing1
        val tiltaksgjennomforingAvsluttetStatus =
            gjennomforing1.copy(id = UUID.randomUUID(), avslutningsstatus = Avslutningsstatus.AVSLUTTET)
        val tiltaksgjennomforingAvsluttetDato = gjennomforing1.copy(
            id = UUID.randomUUID(),
            avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
            sluttDato = LocalDate.of(2023, 1, 31),
        )
        val tiltaksgjennomforingAvbrutt = gjennomforing1.copy(
            id = UUID.randomUUID(),
            avslutningsstatus = Avslutningsstatus.AVBRUTT,
        )
        val tiltaksgjennomforingAvlyst = gjennomforing1.copy(
            id = UUID.randomUUID(),
            avslutningsstatus = Avslutningsstatus.AVLYST,
        )
        val tiltaksgjennomforingPlanlagt = gjennomforing1.copy(
            id = UUID.randomUUID(),
            avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
            startDato = LocalDate.of(2023, 2, 2),
        )

        beforeAny {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingAktiv)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingAvsluttetStatus)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingAvsluttetDato)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingAvbrutt)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingPlanlagt)
            tiltaksgjennomforingRepository.upsert(tiltaksgjennomforingAvlyst)
        }

        test("filtrer på avbrutt") {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)

            val result = tiltaksgjennomforingRepository.getAll(
                filter = defaultFilter.copy(status = Tiltaksgjennomforingsstatus.AVBRUTT),
            ).shouldBeRight()

            result.second shouldHaveSize 1
            result.second[0].id shouldBe tiltaksgjennomforingAvbrutt.id
        }

        test("filtrer på avsluttet") {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)

            val result = tiltaksgjennomforingRepository.getAll(
                filter = defaultFilter.copy(status = Tiltaksgjennomforingsstatus.AVSLUTTET),
            ).shouldBeRight()

            result.second shouldHaveSize 2
            result.second.map { it.id }
                .shouldContainAll(tiltaksgjennomforingAvsluttetDato.id, tiltaksgjennomforingAvsluttetStatus.id)
        }

        test("filtrer på gjennomføres") {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)

            val result = tiltaksgjennomforingRepository.getAll(
                filter = defaultFilter.copy(status = Tiltaksgjennomforingsstatus.GJENNOMFORES),
            ).shouldBeRight()

            result.second shouldHaveSize 1
            result.second[0].id shouldBe tiltaksgjennomforingAktiv.id
        }

        test("filtrer på avlyst") {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)

            val result = tiltaksgjennomforingRepository.getAll(
                filter = defaultFilter.copy(status = Tiltaksgjennomforingsstatus.AVLYST),
            ).shouldBeRight()

            result.second shouldHaveSize 1
            result.second[0].id shouldBe tiltaksgjennomforingAvlyst.id
        }

        test("filtrer på åpent for innsøk") {
            val tiltaksgjennomforingRepository = TiltaksgjennomforingRepository(database.db)

            val result = tiltaksgjennomforingRepository.getAll(
                filter = defaultFilter.copy(status = Tiltaksgjennomforingsstatus.APENT_FOR_INNSOK),
            ).shouldBeRight()

            result.second shouldHaveSize 1
            result.second[0].id shouldBe tiltaksgjennomforingPlanlagt.id
        }
    }

    context("pagination") {
        beforeAny {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            (1..105).forEach {
                tiltaksgjennomforinger.upsert(
                    TiltaksgjennomforingDbo(
                        id = UUID.randomUUID(),
                        navn = "Tiltak - $it",
                        tiltakstypeId = tiltakstype1.id,
                        tiltaksnummer = "$it",
                        virksomhetsnummer = "123456789",
                        arenaAnsvarligEnhet = "2990",
                        avslutningsstatus = Avslutningsstatus.AVSLUTTET,
                        startDato = LocalDate.of(2022, 1, 1),
                        tilgjengelighet = Tilgjengelighetsstatus.Ledig,
                        antallPlasser = null,
                        ansvarlige = emptyList(),
                        enheter = emptyList(),
                    ),
                )
            }
        }

        test("default pagination gets first 50 tiltak") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val (totalCount, items) = tiltaksgjennomforinger.getAll(filter = AdminTiltaksgjennomforingFilter())
                .shouldBeRight()

            items.size shouldBe DEFAULT_PAGINATION_LIMIT
            items.first().navn shouldBe "Tiltak - 1"
            items.last().navn shouldBe "Tiltak - 49"

            totalCount shouldBe 105
        }

        test("pagination with page 4 and size 20 should give tiltak with id 59-76") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val (totalCount, items) = tiltaksgjennomforinger.getAll(
                PaginationParams(
                    4,
                    20,
                ),
                AdminTiltaksgjennomforingFilter(),
            ).shouldBeRight()

            items.size shouldBe 20
            items.first().navn shouldBe "Tiltak - 59"
            items.last().navn shouldBe "Tiltak - 76"

            totalCount shouldBe 105
        }

        test("pagination with page 3 default size should give tiltak with id 95-99") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val (totalCount, items) = tiltaksgjennomforinger.getAll(
                PaginationParams(
                    3,
                ),
                AdminTiltaksgjennomforingFilter(),
            ).shouldBeRight()

            items.size shouldBe 5
            items.first().navn shouldBe "Tiltak - 95"
            items.last().navn shouldBe "Tiltak - 99"

            totalCount shouldBe 105
        }

        test("pagination with default page and size 200 should give tiltak with id 1-105") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database.db)
            val (totalCount, items) = tiltaksgjennomforinger.getAll(
                PaginationParams(
                    nullableLimit = 200,
                ),
                AdminTiltaksgjennomforingFilter(),
            ).shouldBeRight()

            items.size shouldBe 105
            items.first().navn shouldBe "Tiltak - 1"
            items.last().navn shouldBe "Tiltak - 99"

            totalCount shouldBe 105
        }
    }
})
