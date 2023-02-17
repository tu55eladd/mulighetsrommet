package no.nav.mulighetsrommet.arena.adapter

import io.ktor.server.testing.*
import no.nav.mulighetsrommet.arena.adapter.services.ArenaEventService
import no.nav.mulighetsrommet.arena.adapter.tasks.RetryFailedEvents
import no.nav.mulighetsrommet.database.kotest.extensions.createArenaAdapterDatabaseTestSchema
import no.nav.security.mock.oauth2.MockOAuth2Server

fun <R> withArenaAdapterApp(
    oauth: MockOAuth2Server = MockOAuth2Server(),
    config: AppConfig = createTestApplicationConfig(oauth),
    test: suspend ApplicationTestBuilder.() -> R
) {
    testApplication {
        application {
            configure(config)
        }
        test()
    }
}

fun createTestApplicationConfig(oauth: MockOAuth2Server) = AppConfig(
    database = createArenaAdapterDatabaseTestSchema(),
    auth = createAuthConfig(oauth),
    kafka = createKafkaConfig(),
    enableFailedRecordProcessor = false,
    tasks = TaskConfig(
        retryFailedEvents = RetryFailedEvents.Config(
            delayOfMinutes = 1
        )
    ),
    services = ServiceConfig(
        mulighetsrommetApi = ServiceClientConfig(url = "mulighetsrommet-api", scope = ""),
        arenaEventService = ArenaEventService.Config(
            channelCapacity = 0,
            numChannelConsumers = 0,
            maxRetries = 0
        ),
        arenaOrdsProxy = ServiceClientConfig(url = "arena-ords-proxy", scope = "")
    ),
    slack = SlackConfig(
        token = "",
        channel = "",
        enable = false
    )
)

fun createKafkaConfig(): KafkaConfig {
    return KafkaConfig(
        brokerUrl = "localhost:29092",
        consumerGroupId = "mulighetsrommet-kafka-consumer.v1",
        topics = TopicsConfig(
            topicStatePollDelay = 10000,
            consumer = mapOf(
                "tiltakendret" to "tiltakendret",
                "tiltakgjennomforingendret" to "tiltakgjennomforingendret",
                "tiltakdeltakerendret" to "tiltakdeltakerendret",
                "sakendret" to "sakendret",
                "avtaleinfoendret" to "avtaleinfoendret",
            )
        )
    )
}

// Default values for 'iss' og 'aud' in tokens issued by mock-oauth2-server is 'default'.
// These values are set as the default here so that standard tokens issued by MockOAuth2Server works with a minimal amount of setup.
fun createAuthConfig(
    oauth: MockOAuth2Server,
    issuer: String = "default",
    audience: String = "default"
): AuthConfig {
    return AuthConfig(
        azure = AuthProvider(
            issuer = oauth.issuerUrl(issuer).toString(),
            jwksUri = oauth.jwksUrl(issuer).toUri().toString(),
            audience = audience,
            tokenEndpointUrl = oauth.tokenEndpointUrl(issuer).toString()
        )
    )
}
