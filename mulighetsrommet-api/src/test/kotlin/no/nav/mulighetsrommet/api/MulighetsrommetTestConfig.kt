package no.nav.mulighetsrommet.api

import com.sksamuel.hoplite.Masked
import io.ktor.server.testing.*
import no.nav.mulighetsrommet.api.setup.oauth.AzureAd
import no.nav.security.mock.oauth2.MockOAuth2Server

fun <R> withMulighetsrommetApp(
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
    database = createDatabaseConfig(),
    auth = createAuthConfig(oauth),
    sanity = createSanityConfig(),
    veilarboppfolgingConfig = createVeilarboppfolgingConfig(),
)

fun createVeilarboppfolgingConfig(): VeilarboppfolgingConfig {
    return VeilarboppfolgingConfig(
        url = "",
        authenticationScope = ""
    )
}

fun createDatabaseConfig(
    host: String = "localhost",
    port: Int = 5442,
    name: String = "mulighetsrommet-api-db",
    user: String = "valp",
    password: Masked = Masked("valp")
) = DatabaseConfig(host, port, name, null, user, password)

fun createDatabaseConfigWithRandomSchema(
    host: String = "localhost",
    port: Int = 5442,
    name: String = "mulighetsrommet-api-db",
    user: String = "valp",
    password: Masked = Masked("valp")
): DatabaseConfig {
    val schema = "$name-${java.util.UUID.randomUUID()}"
    return DatabaseConfig(host, port, name, schema, user, password)
}

// Default values for 'iss' og 'aud' in tokens issued by mock-oauth2-server is 'default'.
// These values are set as the default here so that standard tokens issued by MockOAuth2Server works with a minimal amount of setup.
fun createAuthConfig(
    oauth: MockOAuth2Server,
    issuer: String = "default",
    audience: String = "default"
) = AuthConfig(
    azure = AuthProvider(
        issuer = oauth.issuerUrl(issuer).toString(),
        jwksUri = oauth.jwksUrl(issuer).toUri().toString(),
        audience = audience,
        azureAd = AzureAd(
            clientId = audience,
            clientSecret = "clientSecret",
            wellKnownConfigurationUrl = oauth.wellKnownUrl(issuer).toString() // "https://fakedings.dev-gcp.nais.io/fake/.well-known/openid-configuration"
        )
    )
)

fun createSanityConfig(): SanityConfig {
    return SanityConfig(
        projectId = "",
        authToken = "",
        dataset = ""
    )
}
