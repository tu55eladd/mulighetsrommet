package no.nav.mulighetsrommet.api.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import no.nav.mulighetsrommet.api.domain.dbo.UtkastDbo
import no.nav.mulighetsrommet.api.domain.dbo.Utkasttype
import no.nav.mulighetsrommet.domain.serializers.LocalDateTimeSerializer
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class UtkastDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    @Serializable(with = UUIDSerializer::class)
    val avtaleId: UUID? = null,
    val opprettetAv: String,
    val utkastData: JsonElement,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null,
    val type: Utkasttype,
) {
    fun toDbo(): UtkastDbo {
        return UtkastDbo(
            id = id,
            opprettetAv = opprettetAv,
            utkastData = utkastData,
            createdAt = createdAt,
            updatedAt = updatedAt,
            type = type,
            avtaleId = avtaleId,
        )
    }
}
