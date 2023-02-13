package no.nav.mulighetsrommet.arena.adapter.repositories

import kotlinx.serialization.json.Json
import kotliquery.Row
import kotliquery.queryOf
import no.nav.mulighetsrommet.arena.adapter.models.db.ArenaEvent
import no.nav.mulighetsrommet.database.Database
import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ArenaEventRepository(private val db: Database) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun upsert(event: ArenaEvent): ArenaEvent {
        @Language("PostgreSQL")
        val query = """
            insert into arena_events(arena_table, arena_id, payload, consumption_status, message, retries)
            values (:arena_table, :arena_id, :payload::jsonb, :status::consumption_status, :message, :retries)
            on conflict (arena_table, arena_id)
            do update set
                payload            = excluded.payload,
                consumption_status = excluded.consumption_status,
                message            = excluded.message,
                retries            = excluded.retries
            returning *
        """.trimIndent()

        return queryOf(query, event.toParameters())
            .map { it.toEvent() }
            .asSingle
            .let { db.run(it)!! }
    }

    fun updateStatus(table: String?, oldStatus: ArenaEvent.ConsumptionStatus?, newStatus: ArenaEvent.ConsumptionStatus) {
        val where = andWhereParameterNotNull(table to "arena_table = :table", oldStatus to "consumption_status = :old_status::consumption_status")

        @Language("PostgreSQL")
        val query = """
            update arena_events
            set consumption_status = :new_status::consumption_status, retries = 0
            $where
        """.trimIndent()

        return queryOf(query, mapOf("table" to table, "old_status" to oldStatus?.name, "new_status" to newStatus.name))
            .asUpdate
            .let { db.run(it) }
    }

    fun get(table: String, id: String): ArenaEvent? {
        logger.info("Getting event id=$id")

        @Language("PostgreSQL")
        val query = """
            select arena_table, arena_id, payload, consumption_status, message, retries
            from arena_events
            where arena_table = :arena_table and arena_id = :arena_id
        """.trimIndent()

        return queryOf(query, mapOf("arena_table" to table, "arena_id" to id))
            .map { it.toEvent() }
            .asSingle
            .let { db.run(it) }
    }

    fun getAll(
        table: String? = null,
        idGreaterThan: String? = null,
        status: ArenaEvent.ConsumptionStatus? = null,
        maxRetries: Int? = null,
        limit: Int = 1000,
    ): List<ArenaEvent> {
        logger.info("Getting events table=$table, idGreaterThan=$idGreaterThan, status=$status, maxRetries=$maxRetries, limit=$limit")

        val where = andWhereParameterNotNull(
            table to "arena_table = :arena_table",
            idGreaterThan to "arena_id > :arena_id",
            status to "consumption_status = :status::consumption_status",
            maxRetries to "retries < :max_retries",
        )

        @Language("PostgreSQL")
        val query = """
            select arena_table, arena_id, payload, consumption_status, message, retries
            from arena_events
            $where
            order by arena_id
            limit :limit
        """.trimIndent()

        return queryOf(
            query,
            mapOf(
                "arena_table" to table,
                "arena_id" to idGreaterThan,
                "status" to status?.name,
                "max_retries" to maxRetries,
                "limit" to limit,
            )
        )
            .map { it.toEvent() }
            .asList
            .let { db.run(it) }
    }

    private fun andWhereParameterNotNull(vararg parts: Pair<Any?, String>): String = parts
        .filter { it.first != null }
        .map { it.second }
        .reduceOrNull { where, part -> "$where and $part" }
        ?.let { "where $it" }
        ?: ""

    private fun ArenaEvent.toParameters() = mapOf(
        "arena_table" to arenaTable,
        "arena_id" to arenaId,
        "payload" to payload.toString(),
        "status" to status.name,
        "message" to message,
        "retries" to retries,
    )

    private fun Row.toEvent() = ArenaEvent(
        arenaTable = string("arena_table"),
        arenaId = string("arena_id"),
        payload = Json.parseToJsonElement(string("payload")),
        status = ArenaEvent.ConsumptionStatus.valueOf(string("consumption_status")),
        message = stringOrNull("message"),
        retries = int("retries"),
    )
}
