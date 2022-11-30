package no.nav.mulighetsrommet.api.repositories

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet.api.utils.DEFAULT_PAGINATION_LIMIT
import no.nav.mulighetsrommet.api.utils.PaginationParams
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseListener
import no.nav.mulighetsrommet.database.kotest.extensions.createApiDatabaseTestSchema
import no.nav.mulighetsrommet.domain.models.Tiltaksgjennomforing
import no.nav.mulighetsrommet.domain.models.Tiltakstype
import java.util.*

class TiltaksgjennomforingRepositoryTest : FunSpec({

    testOrder = TestCaseOrder.Sequential

    val listener = FlywayDatabaseListener(createApiDatabaseTestSchema())

    register(listener)

    val tiltakstype1 = Tiltakstype(
        id = UUID.randomUUID(),
        navn = "Arbeidstrening",
        tiltakskode = "ARBTREN"
    )

    val tiltakstype2 = Tiltakstype(
        id = UUID.randomUUID(),
        navn = "Oppfølging",
        tiltakskode = "INDOPPFOLG"
    )

    val tiltak1 = Tiltaksgjennomforing(
        id = UUID.randomUUID(),
        navn = "Oppfølging",
        tiltakstypeId = tiltakstype1.id,
        tiltaksnummer = "12345",
        virksomhetsnummer = "123456789"
    )

    val tiltak2 = Tiltaksgjennomforing(
        id = UUID.randomUUID(),
        navn = "Trening",
        tiltakstypeId = tiltakstype2.id,
        tiltaksnummer = "54321",
        virksomhetsnummer = "123456789"
    )

    context("CRUD") {
        beforeAny {
            val tiltakstyper = TiltakstypeRepository(listener.db)
            tiltakstyper.save(tiltakstype1)
            tiltakstyper.save(tiltakstype2)
        }

        test("CRUD") {
            val tiltaksgjennomforinger = TiltaksgjennomforingRepository(listener.db)

            tiltaksgjennomforinger.save(tiltak1)
            tiltaksgjennomforinger.save(tiltak2)

            tiltaksgjennomforinger.getTiltaksgjennomforinger().second shouldHaveSize 2
            tiltaksgjennomforinger.getTiltaksgjennomforingById(tiltak1.id) shouldBe tiltak1
            tiltaksgjennomforinger.getTiltaksgjennomforingerByTiltakstypeId(tiltakstype1.id) shouldHaveSize 1

            tiltaksgjennomforinger.delete(tiltak1.id)

            tiltaksgjennomforinger.getTiltaksgjennomforinger().second shouldHaveSize 1
        }
    }

//        TODO: implementer på nytt
//        context("tilgjengelighetsstatus") {
//            context("when tiltak is closed for applications") {
//                beforeAny {
//                    arenaService.createOrUpdate(
//                        tiltak1.copy(
//                            apentForInnsok = false
//                        )
//                    )
//                }
//
//                afterAny {
//                    arenaService.upsertTiltaksgjennomforing(
//                        tiltak1.copy(
//                            apentForInnsok = true
//                        )
//                    )
//                }
//
//                test("should have tilgjengelighet set to STENGT") {
//                    tiltaksgjennomforingService.getTiltaksgjennomforingById(1)?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Stengt
//                }
//            }
//
//            context("when there are no limits to available seats") {
//                beforeAny {
//                    arenaService.upsertTiltaksgjennomforing(
//                        tiltak1.copy(
//                            antallPlasser = null
//                        )
//                    )
//                }
//
//                test("should have tilgjengelighet set to APENT_FOR_INNSOK") {
//                    tiltaksgjennomforingService.getTiltaksgjennomforingById(1)?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Ledig
//                }
//            }
//
//            context("when there are no available seats") {
//                beforeAny {
//                    arenaService.upsertTiltaksgjennomforing(
//                        tiltak1.copy(
//                            antallPlasser = 0
//                        )
//                    )
//                }
//
//                test("should have tilgjengelighet set to VENTELISTE") {
//                    tiltaksgjennomforingService.getTiltaksgjennomforingById(1)?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Venteliste
//                }
//            }
//
//            context("when all available seats are occupied by deltakelser with status DELTAR") {
//                beforeAny {
//                    arenaService.upsertTiltaksgjennomforing(
//                        tiltak1.copy(
//                            antallPlasser = 1
//                        )
//                    )
//
//                    arenaService.upsertDeltaker(
//                        AdapterTiltakdeltaker(
//                            tiltaksdeltakerId = 1,
//                            tiltaksgjennomforingId = tiltak1.tiltaksgjennomforingId,
//                            personId = 1,
//                            status = Deltakerstatus.DELTAR
//                        )
//                    )
//                }
//
//                test("should have tilgjengelighet set to VENTELISTE") {
//                    tiltaksgjennomforingService.getTiltaksgjennomforingById(1)?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Venteliste
//                }
//            }
//
//            context("when deltakelser are no longer DELTAR") {
//                beforeAny {
//                    arenaService.upsertTiltaksgjennomforing(
//                        tiltak1.copy(
//                            antallPlasser = 1
//                        )
//                    )
//
//                    arenaService.upsertDeltaker(
//                        AdapterTiltakdeltaker(
//                            tiltaksdeltakerId = 1,
//                            tiltaksgjennomforingId = tiltak1.tiltaksgjennomforingId,
//                            personId = 1,
//                            status = Deltakerstatus.AVSLUTTET
//                        )
//                    )
//                }
//
//                test("should have tilgjengelighet set to APENT_FOR_INNSOK") {
//                    tiltaksgjennomforingService.getTiltaksgjennomforingById(1)?.tilgjengelighet shouldBe Tilgjengelighetsstatus.Ledig
//                }
//            }
//        }

    context("pagination") {
        listener.db.clean()
        listener.db.migrate()

        val tiltakstyper = TiltakstypeRepository(listener.db)
        tiltakstyper.save(tiltakstype1)

        val tiltaksgjennomforinger = TiltaksgjennomforingRepository(listener.db)
        (1..105).forEach {
            tiltaksgjennomforinger.save(
                Tiltaksgjennomforing(
                    id = UUID.randomUUID(),
                    navn = "$it",
                    tiltakstypeId = tiltakstype1.id,
                    tiltaksnummer = "$it",
                    virksomhetsnummer = "123456789"
                )
            )
        }

        test("default pagination gets first 50 tiltak") {
            val (totalCount, items) = tiltaksgjennomforinger.getTiltaksgjennomforinger()

            items.size shouldBe DEFAULT_PAGINATION_LIMIT
            items.first().navn shouldBe "1"
            items.last().navn shouldBe "50"

            totalCount shouldBe 105
        }

        test("pagination with page 4 and size 20 should give tiltak with id 61-80") {
            val (totalCount, items) = tiltaksgjennomforinger.getTiltaksgjennomforinger(
                PaginationParams(
                    4,
                    20
                )
            )

            items.size shouldBe 20
            items.first().navn shouldBe "61"
            items.last().navn shouldBe "80"

            totalCount shouldBe 105
        }

        test("pagination with page 3 default size should give tiltak with id 101-105") {
            val (totalCount, items) = tiltaksgjennomforinger.getTiltaksgjennomforinger(
                PaginationParams(
                    3
                )
            )

            items.size shouldBe 5
            items.first().navn shouldBe "101"
            items.last().navn shouldBe "105"

            totalCount shouldBe 105
        }

        test("pagination with default page and size 200 should give tiltak with id 1-105") {
            val (totalCount, items) = tiltaksgjennomforinger.getTiltaksgjennomforinger(
                PaginationParams(
                    nullableLimit = 200
                )
            )

            items.size shouldBe 105
            items.first().navn shouldBe "1"
            items.last().navn shouldBe "105"

            totalCount shouldBe 105
        }
    }
})
