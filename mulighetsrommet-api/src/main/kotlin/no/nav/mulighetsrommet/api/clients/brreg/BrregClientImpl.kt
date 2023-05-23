package no.nav.mulighetsrommet.api.clients.brreg

import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.ktor.clients.httpJsonClient
import org.slf4j.LoggerFactory

object OrgnummerUtil {
    fun erOrgnr(verdi: String): Boolean {
        val orgnrPattern = "^[0-9]{9}\$".toRegex()
        return orgnrPattern.matches(verdi)
    }
}

class BrregClientImpl(
    private val baseUrl: String,
    clientEngine: HttpClientEngine = CIO.create(),
) : BrregClient {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val client = httpJsonClient(clientEngine).config {
        install(HttpCache)
    }
    private val size = 20

    data class Config(
        val baseUrl: String,
    )

    override suspend fun hentEnhet(orgnr: String): VirksomhetDto {
        validerOrgnr(orgnr)
        val response = client.get("$baseUrl/enheter/$orgnr")
        val data = tolkRespons<BrregEnhet>(response, orgnr)

        // Vi fikk treff på hovedenhet
        if (data != null) {
            return VirksomhetDto(
                organisasjonsnummer = data.organisasjonsnummer,
                navn = data.navn,
                overordnetEnhet = null,
                underenheter = hentUnderenheterForOverordnetEnhet(orgnr),
            )
        }

        // Ingen treff på hovedenhet, vi sjekker underenheter også
        val underenhetResponse = client.get("$baseUrl/underenheter/$orgnr")
        val underenhetData = tolkRespons<BrregEnhet>(underenhetResponse, orgnr)

        return underenhetData?.let {
            VirksomhetDto(
                organisasjonsnummer = it.organisasjonsnummer,
                navn = it.navn,
                overordnetEnhet = it.overordnetEnhet,
                underenheter = null,
            )
        } ?: throw NotFoundException("Fant ingen enhet i Brreg med orgnr: '$orgnr'")
    }

    override suspend fun sokEtterOverordnetEnheter(orgnr: String): List<VirksomhetDto> {
        val sokEllerOppslag = when (OrgnummerUtil.erOrgnr(orgnr)) {
            true -> "organisasjonsnummer"
            false -> "navn"
        }

        val hovedenhetResponse = client.get("$baseUrl/enheter") {
            parameter("size", size)
            parameter(sokEllerOppslag, orgnr)
        }

        val data = tolkRespons<BrregEmbeddedHovedenheter>(hovedenhetResponse, orgnr)

        return data?._embedded?.enheter?.map {
            VirksomhetDto(
                organisasjonsnummer = it.organisasjonsnummer,
                navn = it.navn,
            )
        } ?: emptyList()
    }

    private suspend fun hentUnderenheterForOverordnetEnhet(orgnrOverordnetEnhet: String): List<VirksomhetDto> {
        validerOrgnr(orgnrOverordnetEnhet)

        val underenheterResponse = client.get("$baseUrl/underenheter") {
            parameter("size", size)
            parameter("overordnetEnhet", orgnrOverordnetEnhet)
        }

        val data = tolkRespons<BrregEmbeddedUnderenheter>(underenheterResponse, orgnrOverordnetEnhet)

        return data?.let {
            it._embedded?.underenheter?.map {
                VirksomhetDto(
                    organisasjonsnummer = it.organisasjonsnummer,
                    navn = it.navn,
                    overordnetEnhet = orgnrOverordnetEnhet,
                    underenheter = null,
                )
            }
        } ?: emptyList()
    }

    private suspend inline fun <reified T> tolkRespons(response: HttpResponse, orgnr: String): T? {
        return when (response.status) {
            HttpStatusCode.OK -> response.body<T>()
            HttpStatusCode.NotFound -> {
                log.debug("Fant ikke enhet i Brreg med organisasjonsnummer: $orgnr")
                null
            }

            else -> throw ResponseException(response, "Unexpected response from Brreg sitt Api")
        }
    }

    private fun validerOrgnr(orgnr: String) {
        if (!OrgnummerUtil.erOrgnr(orgnr)) {
            throw BadRequestException("Orgnr må være 9 siffer. Oppgitt orgnr: '$orgnr'")
        }
    }
}

@Serializable
internal data class BrregEnhet(
    val organisasjonsnummer: String,
    val navn: String,
    val overordnetEnhet: String? = null,
)

@Serializable
internal data class BrregEmbeddedHovedenheter(
    val _embedded: BrregHovedenheter? = null,
)

@Serializable
internal data class BrregHovedenheter(
    val enheter: List<BrregEnhet>,
)

@Serializable
internal data class BrregEmbeddedUnderenheter(
    val _embedded: BrregUnderenheter? = null,
)

@Serializable
internal data class BrregUnderenheter(
    val underenheter: List<BrregEnhet>,
)