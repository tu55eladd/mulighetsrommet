package no.nav.mulighetsrommet.tiltakshistorikk.kafka.amt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.encodeToJsonElement
import no.nav.mulighetsrommet.database.kotest.extensions.FlywayDatabaseTestListener
import no.nav.mulighetsrommet.database.kotest.extensions.truncateAll
import no.nav.mulighetsrommet.domain.dto.amt.AmtDeltakerStatus
import no.nav.mulighetsrommet.domain.dto.amt.AmtDeltakerV1Dto
import no.nav.mulighetsrommet.kafka.KafkaTopicConsumer
import no.nav.mulighetsrommet.tiltakshistorikk.DeltakerRepository
import no.nav.mulighetsrommet.tiltakshistorikk.createDatabaseTestConfig
import no.nav.mulighetsrommet.tiltakshistorikk.kafka.consumers.amt.AmtDeltakerV1TopicConsumer
import java.time.LocalDateTime
import java.util.*

class AmtDeltakerV1TopicConsumerTest : FunSpec({
    val database = extension(FlywayDatabaseTestListener(createDatabaseTestConfig()))

    context("consume deltakere") {
        afterEach {
            database.db.truncateAll()
        }

        val deltakere = DeltakerRepository(database.db)
        val deltakerConsumer = AmtDeltakerV1TopicConsumer(
            config = KafkaTopicConsumer.Config(id = "deltaker", topic = "deltaker"),
            deltakere,
        )

        val deltakelsesdato = LocalDateTime.of(2023, 3, 1, 0, 0, 0)

        val amtDeltaker1 = AmtDeltakerV1Dto(
            id = UUID.randomUUID(),
            gjennomforingId = UUID.randomUUID(),
            personIdent = "10101010100",
            startDato = null,
            sluttDato = null,
            status = AmtDeltakerStatus(
                type = AmtDeltakerStatus.Type.VENTER_PA_OPPSTART,
                aarsak = null,
                opprettetDato = deltakelsesdato,
            ),
            registrertDato = deltakelsesdato,
            endretDato = deltakelsesdato,
            dagerPerUke = 2.5f,
            prosentStilling = null,
        )

        test("upsert deltakere from topic") {
            deltakerConsumer.consume(amtDeltaker1.id, Json.encodeToJsonElement(amtDeltaker1))

            deltakere.getKometDeltakelser(listOf(amtDeltaker1.personIdent)).shouldContainExactly(amtDeltaker1)
        }

        test("delete deltakere for tombstone messages") {
            deltakere.upsertKometDeltaker(amtDeltaker1)

            deltakerConsumer.consume(amtDeltaker1.id, JsonNull)

            deltakere.getKometDeltakelser(listOf(amtDeltaker1.personIdent)).shouldBeEmpty()
        }

        test("delete deltakere that have status FEILREGISTRERT") {
            deltakere.upsertKometDeltaker(amtDeltaker1)

            val feilregistrertDeltaker1 = amtDeltaker1.copy(
                status = AmtDeltakerStatus(
                    type = AmtDeltakerStatus.Type.FEILREGISTRERT,
                    aarsak = null,
                    opprettetDato = LocalDateTime.now(),
                ),
            )
            deltakerConsumer.consume(feilregistrertDeltaker1.id, Json.encodeToJsonElement(feilregistrertDeltaker1))

            deltakere.getKometDeltakelser(listOf(amtDeltaker1.personIdent)).shouldBeEmpty()
        }
    }
})
