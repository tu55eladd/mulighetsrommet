package no.nav.mulighetsrommet.api.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import no.nav.mulighetsrommet.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class Faneinnhold(
    val forHvem: List<Innhold>? = emptyList(),
    val forHvemInfoboks: String? = null,
    val detaljerOgInnhold: List<Innhold>? = emptyList(),
    val detaljerOgInnholdInfoboks: String? = null,
    val pameldingOgVarighet: List<Innhold>? = emptyList(),
    val pameldingOgVarighetInfoboks: String? = null,
)

@Serializable
data class Innhold(
    val style: String? = null,
    val _key: String? = null,
    val level: Int? = null,
    val listItem: String? = null,
    val _type: String? = null,
    val children: List<InnholdChild>? = emptyList(),
    val markDefs: List<InnholdChild>? = emptyList(),
)

@Serializable
data class InnholdChild(
    val text: String? = null,
    val _type: String? = null,
    val _key: String? = null,
    val marks: List<String>? = emptyList(),
    val href: String? = null,
)

@Serializable
data class RegelverkLenke(
    val _id: String? = null,
    val beskrivelse: String? = null,
    val regelverkUrl: String? = null,
    val regelverkLenkeNavn: String? = null,
)

@Serializable
data class SanityInnsatsgruppe(
    val _id: String,
    val tittel: String,
    val nokkel: String,
    val beskrivelse: String,
    val order: Int,
)

@Serializable
data class SanityTiltakstype(
    val _id: String? = null,
    val tiltakstypeNavn: String? = null,
    val beskrivelse: String? = null,
    val innsatsgruppe: SanityInnsatsgruppe? = null,
    val regelverkLenker: List<RegelverkLenke>? = emptyList(),
    val faneinnhold: Faneinnhold? = null,
    val delingMedBruker: String? = null,
)

@Serializable
data class SanityTiltaksgjennomforing(
    val _id: String,
    val tiltakstype: SanityTiltakstype? = null,
    val tiltaksgjennomforingNavn: String,
    val tiltaksnummer: String? = null,
    val beskrivelse: String? = null,
    val lokasjon: String? = null,
    val fylke: String? = null,
    val enheter: List<String>? = emptyList(),
    val kontaktinfoTiltaksansvarlige: List<KontaktinfoTiltaksansvarlige>? = emptyList(),
    val kontaktpersoner: List<SanityKontaktperson>? = emptyList(),
    val faneinnhold: Faneinnhold? = null,
)

@Serializable
data class KontaktinfoTiltaksansvarlige(
    val _rev: String? = null,
    val _type: String? = null,
    val navn: String? = null,
    val telefonnummer: String? = null,
    val _id: String? = null,
    val enhet: String? = null,
    val _updatedAt: String? = null,
    val _createdAt: String? = null,
    val epost: String? = null,
)

@Serializable
data class SanityKontaktperson(
    val navKontaktperson: KontaktinfoTiltaksansvarlige,
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
