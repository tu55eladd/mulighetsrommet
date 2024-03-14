package no.nav.mulighetsrommet.api.repositories

import kotliquery.Row
import kotliquery.queryOf
import no.nav.mulighetsrommet.api.domain.dto.BrregVirksomhetDto
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetDto
import no.nav.mulighetsrommet.api.domain.dto.VirksomhetKontaktperson
import no.nav.mulighetsrommet.api.utils.VirksomhetTil
import no.nav.mulighetsrommet.database.Database
import org.intellij.lang.annotations.Language
import org.slf4j.LoggerFactory
import java.util.*

class VirksomhetRepository(private val db: Database) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /** Upserter kun enheten og tar ikke hensyn til underenheter */
    fun upsert(virksomhet: VirksomhetDto) {
        @Language("PostgreSQL")
        val query = """
            insert into virksomhet(id, organisasjonsnummer, navn, overordnet_enhet, slettet_dato, postnummer, poststed)
            values (:id, :organisasjonsnummer, :navn, :overordnet_enhet, :slettet_dato, :postnummer, :poststed)
            on conflict (id) do update set
                organisasjonsnummer = excluded.organisasjonsnummer,
                navn = excluded.navn,
                overordnet_enhet = excluded.overordnet_enhet,
                slettet_dato = excluded.slettet_dato,
                postnummer = excluded.postnummer,
                poststed = excluded.poststed
            returning *
        """.trimIndent()

        val parameters = virksomhet.run {
            mapOf(
                "id" to id,
                "organisasjonsnummer" to organisasjonsnummer,
                "navn" to navn,
                "overordnet_enhet" to overordnetEnhet,
                "slettet_dato" to slettetDato,
                "postnummer" to postnummer,
                "poststed" to poststed,
            )
        }

        db.run(queryOf(query, parameters).asExecute)
    }

    /** Upserter kun enheten og tar ikke hensyn til underenheter */
    fun upsert(brregVirksomhet: BrregVirksomhetDto) {
        logger.info("Lagrer virksomhet ${brregVirksomhet.organisasjonsnummer}")

        @Language("PostgreSQL")
        val query = """
            insert into virksomhet(organisasjonsnummer, navn, overordnet_enhet, slettet_dato, postnummer, poststed)
            values (:organisasjonsnummer, :navn, :overordnet_enhet, :slettet_dato, :postnummer, :poststed)
            on conflict (organisasjonsnummer) do update set
                navn = excluded.navn,
                overordnet_enhet = excluded.overordnet_enhet,
                slettet_dato = excluded.slettet_dato,
                postnummer = excluded.postnummer,
                poststed = excluded.poststed
            returning *
        """.trimIndent()

        db.transaction { tx ->
            tx.run(queryOf(query, brregVirksomhet.toSqlParameters()).asExecute)
        }
    }

    fun getAll(
        til: VirksomhetTil? = null,
        sok: String? = null,
        overordnetEnhetOrgnr: String? = null,
        slettet: Boolean? = null,
        utenlandsk: Boolean? = null,
    ): List<VirksomhetDto> {
        val join = when (til) {
            VirksomhetTil.AVTALE -> {
                "inner join avtale on avtale.leverandor_virksomhet_id = v.id"
            }

            VirksomhetTil.TILTAKSGJENNOMFORING -> {
                "inner join tiltaksgjennomforing t on t.arrangor_virksomhet_id = v.id"
            }

            else -> ""
        }

        @Language("PostgreSQL")
        val selectVirksomheter = """
            select distinct
                v.id,
                v.organisasjonsnummer,
                v.overordnet_enhet,
                v.navn,
                v.slettet_dato,
                v.postnummer,
                v.poststed
            from virksomhet v
                $join
            where (:sok::text is null or v.navn ilike :sok)
              and (:overordnet_enhet::text is null or v.overordnet_enhet = :overordnet_enhet)
              and (:slettet::boolean is null or v.slettet_dato is not null = :slettet)
              and (:utenlandsk::boolean is null or v.er_utenlandsk_virksomhet = :utenlandsk)
            order by v.navn asc
        """.trimIndent()

        val params = mapOf(
            "sok" to sok?.let { "%$it%" },
            "overordnet_enhet" to overordnetEnhetOrgnr,
            "slettet" to slettet,
            "utenlandsk" to utenlandsk,
        )

        return queryOf(selectVirksomheter, params)
            .map { it.toVirksomhetDto() }
            .asList
            .let { db.run(it) }
    }

    fun get(orgnr: String): VirksomhetDto? {
        @Language("PostgreSQL")
        val selectVirksomhet = """
            select
                v.id,
                v.organisasjonsnummer,
                v.overordnet_enhet,
                v.navn,
                v.slettet_dato,
                v.postnummer,
                v.poststed
            from virksomhet v
            where v.organisasjonsnummer = ?
        """.trimIndent()

        @Language("PostgreSQL")
        val selectUnderenheterTilVirksomhet = """
            select
                v.id,
                v.organisasjonsnummer,
                v.overordnet_enhet,
                v.navn,
                v.slettet_dato,
                v.postnummer,
                v.poststed
            from virksomhet v
            where v.overordnet_enhet = ?
        """.trimIndent()

        val virksomhet = queryOf(selectVirksomhet, orgnr)
            .map { it.toVirksomhetDto() }
            .asSingle
            .let { db.run(it) }

        return if (virksomhet != null) {
            val underenheter = queryOf(selectUnderenheterTilVirksomhet, orgnr)
                .map { it.toVirksomhetDto() }
                .asList
                .let { db.run(it) }
                .takeIf { it.isNotEmpty() }
            virksomhet.copy(underenheter = underenheter)
        } else {
            null
        }
    }

    fun getById(id: UUID): VirksomhetDto {
        @Language("PostgreSQL")
        val query = """
            select
                v.id,
                v.organisasjonsnummer,
                v.overordnet_enhet,
                v.navn,
                v.slettet_dato,
                v.postnummer,
                v.poststed
            from virksomhet v
            where v.id = ?::uuid
        """.trimIndent()

        val virksomhet = queryOf(query, id)
            .map { it.toVirksomhetDto() }
            .asSingle
            .let { db.run(it) }

        return requireNotNull(virksomhet) {
            "Virksomhet med id=$id finnes ikke"
        }
    }

    fun delete(orgnr: String) {
        logger.info("Sletter virksomhet $orgnr")

        @Language("PostgreSQL")
        val query = """
            delete from virksomhet where organisasjonsnummer = ?
        """.trimIndent()

        db.transaction { tx ->
            tx.run(queryOf(query, orgnr).asExecute)
        }
    }

    fun upsertKontaktperson(virksomhetKontaktperson: VirksomhetKontaktperson): VirksomhetKontaktperson {
        @Language("PostgreSQL")
        val upsertVirksomhetKontaktperson = """
            insert into virksomhet_kontaktperson(id, virksomhet_id, navn, telefon, epost, beskrivelse)
            values (:id::uuid, :virksomhet_id, :navn, :telefon, :epost, :beskrivelse)
            on conflict (id) do update set
                navn                = excluded.navn,
                virksomhet_id       = excluded.virksomhet_id,
                telefon             = excluded.telefon,
                epost               = excluded.epost,
                beskrivelse         = excluded.beskrivelse
            returning *
        """.trimIndent()

        return queryOf(upsertVirksomhetKontaktperson, virksomhetKontaktperson.toSqlParameters())
            .map { it.toVirksomhetKontaktperson() }
            .asSingle
            .let { db.run(it)!! }
    }

    fun koblingerTilKontaktperson(id: UUID): Pair<List<UUID>, List<UUID>> {
        @Language("PostgreSQL")
        val gjennomforingQuery = """
            select tiltaksgjennomforing_id from tiltaksgjennomforing_virksomhet_kontaktperson where virksomhet_kontaktperson_id = ?
        """.trimIndent()

        val gjennomforinger = queryOf(gjennomforingQuery, id)
            .map { it.uuid("tiltaksgjennomforing_id") }
            .asList
            .let { db.run(it) }

        @Language("PostgreSQL")
        val avtaleQuery = """
            select id from avtale tg where leverandor_kontaktperson_id = ?
        """.trimIndent()

        val avtaler = queryOf(avtaleQuery, id)
            .map { it.uuid("id") }
            .asList
            .let { db.run(it) }

        return gjennomforinger to avtaler
    }

    fun deleteKontaktperson(id: UUID) {
        @Language("PostgreSQL")
        val query = """
            delete from virksomhet_kontaktperson where id = ?
        """.trimIndent()

        queryOf(query, id)
            .asUpdate
            .let { db.run(it) }
    }

    fun getKontaktpersoner(virksomhetId: UUID): List<VirksomhetKontaktperson> {
        @Language("PostgreSQL")
        val query = """
            select
                vk.id,
                vk.virksomhet_id,
                vk.navn,
                vk.telefon,
                vk.epost,
                vk.beskrivelse
            from virksomhet_kontaktperson vk
            where vk.virksomhet_id = ?::uuid
        """.trimIndent()

        return queryOf(query, virksomhetId)
            .map { it.toVirksomhetKontaktperson() }
            .asList
            .let { db.run(it) }
    }

    private fun Row.toVirksomhetDto() = VirksomhetDto(
        id = uuid("id"),
        organisasjonsnummer = string("organisasjonsnummer"),
        navn = string("navn"),
        overordnetEnhet = stringOrNull("overordnet_enhet"),
        slettetDato = localDateOrNull("slettet_dato"),
        postnummer = stringOrNull("postnummer"),
        poststed = stringOrNull("poststed"),
    )

    private fun Row.toVirksomhetKontaktperson() = VirksomhetKontaktperson(
        id = uuid("id"),
        virksomhetId = uuid("virksomhet_id"),
        navn = string("navn"),
        telefon = stringOrNull("telefon"),
        epost = string("epost"),
        beskrivelse = stringOrNull("beskrivelse"),
    )

    private fun BrregVirksomhetDto.toSqlParameters() = mapOf(
        "organisasjonsnummer" to organisasjonsnummer,
        "navn" to navn,
        "overordnet_enhet" to overordnetEnhet,
        "slettet_dato" to slettetDato,
        "postnummer" to postnummer,
        "poststed" to poststed,
    )

    private fun VirksomhetKontaktperson.toSqlParameters() = mapOf(
        "id" to id,
        "virksomhet_id" to virksomhetId,
        "navn" to navn,
        "telefon" to telefon,
        "epost" to epost,
        "beskrivelse" to beskrivelse,
    )
}
