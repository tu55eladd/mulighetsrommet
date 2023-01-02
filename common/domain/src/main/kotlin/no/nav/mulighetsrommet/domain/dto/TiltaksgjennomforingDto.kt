package no.nav.mulighetsrommet.domain.dto

import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.domain.serializers.LocalDateSerializer
import no.nav.mulighetsrommet.domain.serializers.UUIDSerializer
import java.time.LocalDate
import java.util.*

@Serializable
data class TiltaksgjennomforingDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val tiltakstype: TiltakstypeDto,
    val navn: String?,
    @Serializable(with = LocalDateSerializer::class)
    val startDato: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val sluttDato: LocalDate? = null,
) {
    companion object {
        fun from(tiltaksgjennomforing: TiltaksgjennomforingAdminDto) = tiltaksgjennomforing.run {
            TiltaksgjennomforingDto(
                id = id,
                tiltakstype = tiltakstype,
                navn = navn,
                startDato = fraDato?.let { LocalDate.from(it) },
                sluttDato = tilDato?.let { LocalDate.from(it) },
            )
        }
    }
}
