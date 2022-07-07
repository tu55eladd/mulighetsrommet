package no.nav.mulighetsrommet.api.setup.oauth

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.setup.http.baseClient
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(AzureAdClient::class.java)

class AzureAdClient(
    private val config: AzureAd,
    private val httpClient: HttpClient = baseClient()
) {

    private suspend inline fun fetchAccessToken(formParameters: Parameters): Result<AccessToken, ThrowableErrorMessage> =
        runCatching {
            httpClient.submitForm(
                url = config.openIdConfiguration.token_endpoint,
                formParameters = formParameters
            ).body() as AccessToken
        }.fold(
            onSuccess = { result -> Ok(result) },
            onFailure = { error -> error.handleError("Could not fetch access token from authority endpoint") }
        )

    private suspend fun Throwable.handleError(message: String): Err<ThrowableErrorMessage> {
        val responseBody: String? = when (this) {
            is ResponseException -> this.response.bodyAsText()
            else -> null
        }
        return "$message. response body: $responseBody"
            .also { errorMessage -> logger.error(errorMessage, this) }
            .let { errorMessage -> Err(ThrowableErrorMessage(errorMessage, this)) }
    }

    // Service-to-service access token request (client credentials grant)
    suspend fun getAccessTokenForResource(scopes: List<String>): Result<AccessToken, ThrowableErrorMessage> =
        fetchAccessToken(
            Parameters.build {
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                append("scope", scopes.joinToString(separator = " "))
                append("grant_type", "client_credentials")
            }
        )

    // (on-behalf-of flow)
    suspend fun getOnBehalfOfAccessTokenForResource(
        scopes: List<String>,
        accessToken: String
    ): Result<AccessToken, ThrowableErrorMessage> =
        fetchAccessToken(
            Parameters.build {
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                append("scope", scopes.joinToString(separator = " "))
                append("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                append("requested_token_use", "on_behalf_of")
                append("assertion", accessToken)
                append("assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
            }
        )
}

@Serializable
data class AccessToken(
    val access_token: String,
    val expires_in: Int,
    val token_type: String
)
