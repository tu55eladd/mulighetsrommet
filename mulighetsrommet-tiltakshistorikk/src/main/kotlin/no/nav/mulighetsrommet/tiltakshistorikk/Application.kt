package no.nav.mulighetsrommet.tiltakshistorikk

import io.ktor.server.application.*
import io.ktor.server.routing.*
import no.nav.common.kafka.util.KafkaPropertiesBuilder
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.mulighetsrommet.database.Database
import no.nav.mulighetsrommet.database.FlywayMigrationManager
import no.nav.mulighetsrommet.env.NaisEnv
import no.nav.mulighetsrommet.hoplite.loadConfiguration
import no.nav.mulighetsrommet.kafka.KafkaConsumerOrchestrator
import no.nav.mulighetsrommet.ktor.plugins.configureMonitoring
import no.nav.mulighetsrommet.ktor.startKtorApplication
import no.nav.mulighetsrommet.tiltakshistorikk.kafka.consumers.amt.AmtDeltakerV1TopicConsumer
import no.nav.mulighetsrommet.tiltakshistorikk.plugins.configureAuthentication
import no.nav.mulighetsrommet.tiltakshistorikk.plugins.configureHTTP
import no.nav.mulighetsrommet.tiltakshistorikk.plugins.configureSerialization
import org.apache.kafka.common.serialization.ByteArrayDeserializer

fun main() {
    val (server, app) = loadConfiguration<Config>()

    startKtorApplication(server) {
        configure(app)
    }
}

fun Application.configure(config: AppConfig) {
    val db = Database(config.database)

    FlywayMigrationManager(config.flyway).migrate(db)

    configureAuthentication(config.auth)
    configureSerialization()
    configureMonitoring({ db.isHealthy() })
    configureHTTP()

    val deltakerRepository = DeltakerRepository(db)

    configureKafka(config.kafka, db, deltakerRepository)

    routing {
        tiltakshistorikkRoutes(deltakerRepository)
    }
}

fun configureKafka(
    config: KafkaConfig,
    db: Database,
    deltakerRepository: DeltakerRepository,
) {
    val properties = when (NaisEnv.current()) {
        NaisEnv.Local -> KafkaPropertiesBuilder.consumerBuilder()
            .withBaseProperties()
            .withConsumerGroupId(config.consumerGroupId)
            .withBrokerUrl(config.brokerUrl)
            .withDeserializers(ByteArrayDeserializer::class.java, ByteArrayDeserializer::class.java)
            .build()

        else -> KafkaPropertiesPreset.aivenDefaultConsumerProperties(config.consumerGroupId)
    }

    val consumers = listOf(
        AmtDeltakerV1TopicConsumer(config = config.consumers.amtDeltakerV1, deltakerRepository = deltakerRepository),
    )
    KafkaConsumerOrchestrator(
        consumerPreset = properties,
        db = db,
        consumers = consumers,
    )
}
