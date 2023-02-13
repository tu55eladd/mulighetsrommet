package no.nav.mulighetsrommet.arena.adapter.consumers

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet.arena.adapter.ConsumerConfig
import no.nav.mulighetsrommet.arena.adapter.fixtures.TiltakstypeFixtures
import no.nav.mulighetsrommet.arena.adapter.fixtures.createArenaAvtaleInfoEvent
import no.nav.mulighetsrommet.arena.adapter.models.ArenaEventData
import no.nav.mulighetsrommet.arena.adapter.models.arena.Avtalestatuskode
import no.nav.mulighetsrommet.arena.adapter.models.db.ArenaEvent
import no.nav.mulighetsrommet.arena.adapter.models.db.Avtale
import no.nav.mulighetsrommet.arena.adapter.repositories.*
import no.nav.mulighetsrommet.arena.adapter.services.ArenaEntityService
import no.nav.mulighetsrommet.database.Database
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener
import no.nav.mulighetsrommet.database.kotest.extensions.createArenaAdapterDatabaseTestSchema

class AvtaleInfoEndretConsumerTest : FunSpec({

    testOrder = TestCaseOrder.Sequential

    val database = extension(FlywayDatabaseTestListener(createArenaAdapterDatabaseTestSchema()))

    beforeEach {
        database.db.migrate()
    }

    afterEach {
        database.db.clean()
    }

    context("when dependent events has not been processed") {
        test("should save the event with status Failed when dependent tiltakstype is missing") {
            val consumer = createConsumer(database.db)

            val event = consumer.processEvent(createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert))

            event.status shouldBe ArenaEvent.ConsumptionStatus.Failed
            database.assertThat("avtale").isEmpty
        }
    }

    context("when dependent tiltakstype has been processed") {
        beforeEach {
            val tiltakstyper = TiltakstypeRepository(database.db)
            tiltakstyper.upsert(TiltakstypeFixtures.Gruppe)
        }

        test("ignore avtaler when required fields are missing") {
            val consumer = createConsumer(database.db)

            val events = listOf(
                createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert) {
                    it.copy(DATO_FRA = null)
                },
                createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert) {
                    it.copy(DATO_TIL = null)
                },
                createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert) {
                    it.copy(ARBGIV_ID_LEVERANDOR = null)
                },
            )

            events.forEach {
                consumer.processEvent(it).status shouldBe ArenaEvent.ConsumptionStatus.Ignored
            }
            database.assertThat("avtale").isEmpty
        }

        test("ignore avtaler ended before 2023") {
            val consumer = createConsumer(database.db)

            val event = createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert) {
                it.copy(DATO_TIL = "2022-12-31 00:00:00")
            }
            consumer.processEvent(event).status shouldBe ArenaEvent.ConsumptionStatus.Ignored
            database.assertThat("avtale").isEmpty
        }

        test("should treat all operations as upserts") {
            val consumer = createConsumer(database.db)

            val e1 = createArenaAvtaleInfoEvent(ArenaEventData.Operation.Insert)
            consumer.processEvent(e1).status shouldBe ArenaEvent.ConsumptionStatus.Processed
            database.assertThat("avtale").row().value("status").isEqualTo(Avtale.Status.Aktiv.name)

            val e2 = createArenaAvtaleInfoEvent(ArenaEventData.Operation.Update) {
                it.copy(AVTALESTATUSKODE = Avtalestatuskode.Planlagt)
            }
            consumer.processEvent(e2).status shouldBe ArenaEvent.ConsumptionStatus.Processed
            database.assertThat("avtale").row().value("status").isEqualTo(Avtale.Status.Planlagt.name)

            val e3 = createArenaAvtaleInfoEvent(ArenaEventData.Operation.Update) {
                it.copy(AVTALESTATUSKODE = Avtalestatuskode.Avsluttet)
            }
            consumer.processEvent(e3).status shouldBe ArenaEvent.ConsumptionStatus.Processed
            database.assertThat("avtale").row().value("status").isEqualTo(Avtale.Status.Avsluttet.name)
        }
    }
})

private fun createConsumer(db: Database): AvtaleInfoEndretConsumer {
    val entities = ArenaEntityService(
        events = ArenaEventRepository(db),
        mappings = ArenaEntityMappingRepository(db),
        tiltakstyper = TiltakstypeRepository(db),
        saker = SakRepository(db),
        tiltaksgjennomforinger = TiltaksgjennomforingRepository(db),
        deltakere = DeltakerRepository(db),
        avtaler = AvtaleRepository(db),
    )

    return AvtaleInfoEndretConsumer(
        ConsumerConfig("avtaleinfoendret", "avtaleinfoendret"),
        ArenaEventRepository(db),
        entities,
    )
}
