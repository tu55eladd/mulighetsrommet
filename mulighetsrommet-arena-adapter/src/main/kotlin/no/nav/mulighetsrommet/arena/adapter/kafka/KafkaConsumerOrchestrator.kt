package no.nav.mulighetsrommet.arena.adapter.kafka

import kotlinx.serialization.json.JsonElement
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider
import no.nav.common.kafka.consumer.KafkaConsumerClient
import no.nav.common.kafka.consumer.feilhandtering.KafkaConsumerRecordProcessor
import no.nav.common.kafka.consumer.feilhandtering.util.KafkaConsumerRecordProcessorBuilder
import no.nav.common.kafka.consumer.util.ConsumerUtils.findConsumerConfigsWithStoreOnFailure
import no.nav.common.kafka.consumer.util.KafkaConsumerClientBuilder
import no.nav.common.kafka.consumer.util.deserializer.Deserializers.stringDeserializer
import no.nav.mulighetsrommet.arena.adapter.Database
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer

class KafkaConsumerOrchestrator(
    consumerPreset: Properties,
    db: Database,
    val consumers: List<TopicConsumer<*>>
) {

    private val logger = LoggerFactory.getLogger(KafkaConsumerOrchestrator::class.java)
    private val consumerClient: KafkaConsumerClient
    private val consumerRecordProcessor: KafkaConsumerRecordProcessor

    init {
        logger.debug("Initializing Kafka")

        val kafkaConsumerRepository = KafkaConsumerRepository(db)
        val consumerTopicsConfig = configureConsumersTopics(kafkaConsumerRepository)
        val lockProvider = JdbcLockProvider(db.dataSource)

        consumerClient = KafkaConsumerClientBuilder.builder()
            .withProperties(consumerPreset)
            .withTopicConfigs(consumerTopicsConfig)
            .build()

        consumerRecordProcessor = KafkaConsumerRecordProcessorBuilder
            .builder()
            .withLockProvider(lockProvider)
            .withKafkaConsumerRepository(kafkaConsumerRepository)
            .withConsumerConfigs(findConsumerConfigsWithStoreOnFailure(consumerTopicsConfig))
            .build()
    }

    fun enableTopicConsumption() {
        consumerClient.start()
        logger.debug("Started kafka consumer client")
    }

    fun enableFailedRecordProcessor() {
        consumerRecordProcessor.start()
        logger.debug("Started kafka consumer record processor")
    }

    fun disableTopicConsumption() {
        consumerClient.stop()
        logger.debug("Stopped kafka consumer client")
    }

    fun disableFailedRecordProcessor() {
        consumerRecordProcessor.close()
        logger.debug("Stopped kafka processors")
    }

    private fun configureConsumersTopics(repository: KafkaConsumerRepository): List<KafkaConsumerClientBuilder.TopicConfig<String, JsonElement>> {
        return consumers.map { consumer ->
            KafkaConsumerClientBuilder.TopicConfig<String, JsonElement>()
                .withStoreOnFailure(repository)
                .withLogging()
                .withConsumerConfig(
                    consumer.topic,
                    stringDeserializer(),
                    JsonElementDeserializer(),
                    Consumer { event ->
                        consumer.processEvent(event.value())
                    }
                )
        }
    }
}
