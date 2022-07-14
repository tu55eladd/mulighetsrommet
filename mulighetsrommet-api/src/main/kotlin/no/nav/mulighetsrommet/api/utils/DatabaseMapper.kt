package no.nav.mulighetsrommet.api.utils

import kotliquery.Row
import no.nav.mulighetsrommet.domain.*
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltak
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltakdeltaker
import no.nav.mulighetsrommet.domain.adapter.AdapterTiltaksgjennomforing

object DatabaseMapper {

    fun toTiltakstype(row: Row): Tiltakstype =
        Tiltakstype(
            id = row.int("id"),
            navn = row.string("navn"),
            innsatsgruppe = row.int("innsatsgruppe_id"),
            sanityId = row.intOrNull("sanity_id"),
            tiltakskode = row.string("tiltakskode"),
            fraDato = row.localDateTimeOrNull("fra_dato"),
            tilDato = row.localDateTimeOrNull("til_dato"),
        )

    fun toTiltaksgjennomforing(row: Row): Tiltaksgjennomforing =
        Tiltaksgjennomforing(
            id = row.int("id"),
            navn = row.string("navn"),
            tiltaksnummer = row.int("tiltaksnummer"),
            arrangorId = row.intOrNull("arrangor_id"),
            tiltakskode = row.string("tiltakskode"),
            arenaId = row.int("arena_id"),
            sakId = row.int("sak_id"),
            sanityId = row.intOrNull("sanity_id"),
            fraDato = row.localDateTimeOrNull("fra_dato"),
            tilDato = row.localDateTimeOrNull("til_dato"),
        )

    fun toInnsatsgruppe(row: Row): Innsatsgruppe = Innsatsgruppe(
        id = row.int("id"),
        navn = row.string("navn"),
    )

    fun toDeltaker(row: Row): Deltaker = Deltaker(
        id = row.int("id"),
        arenaId = row.int("arena_id"),
        tiltaksgjennomforingId = row.int("tiltaksgjennomforing_id"),
        personId = row.int("person_id"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        status = Deltakerstatus.valueOf(row.string("status"))
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
        navn = row.string("navn"),
        tiltakskode = row.string("tiltakskode"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        arrangorId = row.int("arrangor_id"),
        tiltaksnummer = row.int("tiltaksnummer"),
        sakId = row.int("sak_id")
    )

    fun toAdapterTiltakdeltaker(row: Row): AdapterTiltakdeltaker = AdapterTiltakdeltaker(
        id = row.int("arena_id"),
        tiltaksgjennomforingId = row.int("tiltaksgjennomforing_id"),
        personId = row.int("person_id"),
        fraDato = row.localDateTimeOrNull("fra_dato"),
        tilDato = row.localDateTimeOrNull("til_dato"),
        status = Deltakerstatus.valueOf(row.string("status"))
    )
}
