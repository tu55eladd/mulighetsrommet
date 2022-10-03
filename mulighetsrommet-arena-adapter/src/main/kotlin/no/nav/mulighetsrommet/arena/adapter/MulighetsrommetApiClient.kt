package no.nav.mulighetsrommet.arena.adapter

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.slf4j.LoggerFactory

class MulighetsrommetApiClient(
    engine: HttpClientEngine = CIO.create(),
    maxRetries: Int = 5,
    baseUri: String,
    private val getToken: () -> String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val client: HttpClient

    init {
        client = HttpClient(engine) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(HttpRequestRetry) {
                retryIf(maxRetries) { _, response ->
                    response.status.value.let { it in 500..599 } || response.status == HttpStatusCode.Conflict
                }

                exponentialDelay()

                modifyRequest {
                    response?.let {
                        logger.info("Request failed with response_status=${it.status}")
                    }
                    logger.info("Retrying request method=${request.method.value}, url=${request.url.buildString()}")
                }
            }
            defaultRequest {
                contentType(ContentType.Application.Json)

                url.takeFrom(
                    URLBuilder().takeFrom(baseUri).apply {
                        encodedPath += url.encodedPath
                    }
                )
            }
        }
    }

    internal suspend inline fun <reified T> sendRequest(
        method: HttpMethod,
        requestUri: String,
        payload: T,
        isValidResponse: HttpResponse.() -> Boolean = { status.isSuccess() },
    ): HttpResponse {
        val response = client.request(requestUri) {
            bearerAuth(getToken())
            this.method = method
            setBody(payload)
        }

        if (!isValidResponse(response)) {
            throw ResponseException(response, response.bodyAsText())
        }

        return response
    }
}
