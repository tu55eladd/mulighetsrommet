package no.nav.mulighetsrommet.domain.dto

import no.nav.mulighetsrommet.domain.dbo.Avslutningsstatus
import java.time.LocalDate

enum class Avtalestatus {
    Aktiv,
    Avsluttet,
    Avbrutt,
    ;

    companion object {
        fun resolveFromDatesAndAvslutningsstatus(
            now: LocalDate,
            startDato: LocalDate,
            sluttDato: LocalDate,
            avslutningsstatus: Avslutningsstatus,
        ): Avtalestatus = when {
            avslutningsstatus == Avslutningsstatus.AVBRUTT -> Avbrutt
            avslutningsstatus == Avslutningsstatus.AVSLUTTET -> Avsluttet
            now <= sluttDato -> Aktiv
            else -> Avsluttet
        }
    }
}
