package no.nav.mulighetsrommet.api.clients.sanity

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class SanityClient(engine: HttpClientEngine = CIO.create(), val config: Config) {

    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config(
        val projectId: String,
        val dataset: String,
        val apiVersion: String,
        val token: String?,
    ) {
        private val baseUrl
            get() = "https://$projectId.apicdn.sanity.io/$apiVersion"

        val queryUrl
            get() = "$baseUrl/data/query/$dataset"

        val mutationUrl
            get() = "$baseUrl/data/mutate/$dataset"
    }

    @Serializable
    data class QueryResponse<T>(
        val ms: Int,
        val query: String,
        val result: T,
    )

    enum class MutationVisibility {
        Sync,
        Async,
        Deferred,
    }

    private val client: HttpClient = HttpClient(engine) {
        expectSuccess = true

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
            modifyRequest {
                response?.let {
                    logger.warn("Request failed with response status=${it.status}")
                }
                logger.info("Retrying request method=${request.method.value}, url=${request.url.buildString()}")
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            config.token?.let { bearerAuth(it) }
        }
    }

    internal suspend inline fun <reified T> getMany(query: String): QueryResponse<List<T>> {
        val response = client.get(config.queryUrl) {
            url {
                parameters.append("query", query)
            }
        }

        return response.body()
    }

    internal suspend inline fun <reified T> mutate(
        mutation: T,
        returnIds: Boolean = false,
        returnDocuments: Boolean = false,
        dryRun: Boolean = false,
        visibility: MutationVisibility = MutationVisibility.Sync,
    ): HttpResponse {
        val response = client.post(config.mutationUrl) {
            setBody(mutation)
            url.parameters.apply {
                append("returnIds", returnIds.toString())
                append("returnDocuments", returnDocuments.toString())
                append("dryRun", dryRun.toString())
                append("visibility", visibility.name.lowercase())
            }
        }

        return response
    }
}