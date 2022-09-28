package no.nav.mulighetsrommet.api.utils

import kotliquery.Row
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltak
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltakdeltaker
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltaksgjennomforing
import no.nav.mulighetsrommet.domain.models.*
import no.nav.mulighetsrommet.domain.models.Tiltaksgjennomforing.Tilgjengelighetsstatus

object DatabaseMapper {

    fun toTiltakstype(row: Row): Tiltakstype =
        Tiltakstype(
            id = row.int("id"),
            navn = row.string("navn"),
            innsatsgruppe = row.int("innsatsgruppe_id"),
            tiltakskode = row.string("tiltakskode")
        )

    fun toTiltaksgjennomforing(row: Row): Tiltaksgjennomforing =
        Tiltaksgjennomforing(
            id = row.int("id"),
            navn = row.string("navn"),
            tiltaksnummer = row.int("tiltaksnummer"),
            tiltakskode = row.string("tiltakskode"),
            aar = row.int("aar"),
            tilgjengelighet = Tilgjengelighetsstatus.valueOf(
                row.string("tilgjengelighet")
            )
        )

    fun toInnsatsgruppe(row: Row): Innsatsgruppe = Innsatsgruppe(
        id = row.int("id"),
        navn = row.string("navn")
    )

    fun toDeltaker(row: Row): Deltaker = Deltaker(
        id = row.int("id"),
        tiltaksgjennomforingId = row.int("tiltaksgjennomforing_id"),
        personId = row.int("person_id"),
        status = Deltakerstatus.valueOf(row.string("status"))
    )

    fun toBrukerHistorikk(row: Row): HistorikkForDeltaker = HistorikkForDeltaker(
        id = row.string("id"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        status = Deltakerstatus.valueOf(row.string("status")),
        tiltaksnavn = row.string("navn"),
        tiltaksnummer = row.string("tiltaksnummer"),
        tiltakstype = row.string("tiltakstype")
    )

    // mulighetsrommet-arena-adapter specific
    fun toAdapterTiltak(row: Row): AdapterTiltak = AdapterTiltak(
        navn = row.string("navn"),
        innsatsgruppe = row.int("innsatsgruppe_id"),
        tiltakskode = row.string("tiltakskode"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato")
    )

    fun toAdapterTiltaksgjennomforing(row: Row): AdapterTiltaksgjennomforing = AdapterTiltaksgjennomforing(
        id = row.int("arena_id"),
        navn = row.stringOrNull("navn"),
        tiltakskode = row.string("tiltakskode"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        arrangorId = row.intOrNull("arrangor_id"),
        sakId = row.int("sak_id"),
        apentForInnsok = row.boolean("apent_for_innsok"),
        antallPlasser = row.intOrNull("antall_plasser")
    )

    fun toAdapterTiltakdeltaker(row: Row): AdapterTiltakdeltaker = AdapterTiltakdeltaker(
        id = row.int("arena_id"),
        tiltaksgjennomforingId = row.int("tiltaksgjennomforing_id"),
        personId = row.int("person_id"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        status = Deltakerstatus.valueOf(row.string("status"))
    )

    fun toDelMedBruker(row: Row): DelMedBruker = DelMedBruker(
        id = row.string("id"),
        bruker_fnr = row.string("bruker_fnr"),
        navident = row.string("navident"),
        tiltaksnummer = row.string("tiltaksnummer"),
        dialogId = row.string("dialogId"),
        created_at = row.localDateTime("created_at"),
        updated_at = row.localDateTime("updated_at"),
        created_by = row.string("created_by"),
        updated_by = row.string("updated_by")
    )
}
