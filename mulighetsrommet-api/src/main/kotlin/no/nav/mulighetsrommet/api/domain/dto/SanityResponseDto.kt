package no.nav.mulighetsrommet.api.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import no.nav.mulighetsrommet.domain.dto.Faneinnhold
import no.nav.mulighetsrommet.domain.dto.Innsatsgruppe
import no.nav.mulighetsrommet.domain.dto.Organisasjonsnummer
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import no.nav.mulighetsrommet.serialization.json.JsonIgnoreUnknownKeys
import java.util.*

@Serializable
data class RegelverkLenke(
    val _id: String? = null,
    val beskrivelse: String? = null,
    val regelverkUrl: String? = null,
    val regelverkLenkeNavn: String? = null,
)

@Serializable
data class SanityTiltakstype(
    val _id: String,
    val tiltakstypeNavn: String,
    val beskrivelse: String? = null,
    val innsatsgrupper: Set<Innsatsgruppe>? = null,
    val regelverkLenker: List<RegelverkLenke>? = null,
    val faneinnhold: Faneinnhold? = null,
    val delingMedBruker: String? = null,
    val kanKombineresMed: List<String> = emptyList(),
)

@Serializable
data class SanityTiltaksgjennomforing(
    val _id: String,
    val tiltakstype: SanityTiltakstype,
    val tiltaksgjennomforingNavn: String? = null,
    val tiltaksnummer: String? = null,
    val beskrivelse: String? = null,
    val stedForGjennomforing: String? = null,
    val fylke: String? = null,
    val enheter: List<String?>? = null,
    val arrangor: SanityArrangor? = null,
    val kontaktpersoner: List<SanityKontaktperson>? = null,
    val faneinnhold: Faneinnhold? = null,
    val delingMedBruker: String? = null,
)

@Serializable
data class SanityArrangor(
    @Serializable(with = UUIDSerializer::class)
    val _id: UUID,
    val navn: String,
    val organisasjonsnummer: Organisasjonsnummer? = null,
    val kontaktpersoner: List<SanityArrangorKontaktperson> = emptyList(),
)

@Serializable
data class SanityArrangorKontaktperson(
    @Serializable(with = UUIDSerializer::class)
    val _id: UUID,
    val navn: String,
    val telefon: String? = null,
    val epost: String,
)

@Serializable
data class KontaktinfoTiltaksansvarlig(
    val _rev: String? = null,
    val _type: String? = null,
    val navn: String? = null,
    val telefonnummer: String? = null,
    val _id: String? = null,
    val enhet: String? = null,
    val _updatedAt: String? = null,
    val _createdAt: String? = null,
    val epost: String? = null,
    val beskrivelse: String? = null,
)

@Serializable
data class SanityKontaktperson(
    val navKontaktperson: KontaktinfoTiltaksansvarlig? = null,
    val enheter: List<String>,
)

@Serializable
data class EnhetRef(
    val _type: String = "reference",
    val _ref: String,
    val _key: String? = null,
)

@Serializable
data class TiltaksnummerSlug(
    val current: String,
    val _type: String = "slug",
)

@Serializable(with = SanityReponseSerializer::class)
sealed class SanityResponse {
    @Serializable
    data class Result(
        val ms: Int,
        val query: String,
        val result: JsonElement,
    ) : SanityResponse() {
        inline fun <reified T> decode(): T {
            return JsonIgnoreUnknownKeys.decodeFromJsonElement(result)
        }
    }

    @Serializable
    data class Error(
        val error: JsonObject,
    ) : SanityResponse()
}

object SanityReponseSerializer : JsonContentPolymorphicSerializer<SanityResponse>(SanityResponse::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "result" in element.jsonObject -> SanityResponse.Result.serializer()
        else -> SanityResponse.Error.serializer()
    }
}
