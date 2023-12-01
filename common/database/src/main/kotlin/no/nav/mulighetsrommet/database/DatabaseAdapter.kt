package no.nav.mulighetsrommet.database

import com.codahale.metrics.health.HealthCheckRegistry
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotliquery.Session
import kotliquery.TransactionalSession
import kotliquery.action.*
import kotliquery.sessionOf
import kotliquery.using
import java.sql.Array
import java.util.*
import javax.sql.DataSource

open class DatabaseAdapter(config: DatabaseConfig) : Database {

    data class Config(
        override val host: String,
        override val port: Int,
        override val name: String,
        override val schema: String?,
        override val user: String,
        override val password: Password,
        override val maximumPoolSize: Int,
        override val googleCloudSqlInstance: String?,
    ) : DatabaseConfig

    private val dataSource: HikariDataSource

    private val session: Session
        get() = sessionOf(dataSource, strict = true)

    init {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            config.schema?.let {
                schema = config.schema
            }
            driverClassName = "org.postgresql.Driver"
            username = config.user
            password = config.password.value
            maximumPoolSize = config.maximumPoolSize
            healthCheckRegistry = HealthCheckRegistry()

            config.googleCloudSqlInstance?.let {
                dataSourceProperties = Properties().apply {
                    setProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory")
                    setProperty("cloudSqlInstance", config.googleCloudSqlInstance)
                }
            }

            validate()
        }
        dataSource = HikariDataSource(hikariConfig)
    }

    fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }

    override fun getDatasource(): DataSource {
        return dataSource
    }

    override fun isHealthy(): Boolean {
        return (dataSource.healthCheckRegistry as? HealthCheckRegistry)
            ?.runHealthChecks()
            ?.all { it.value.isHealthy }
            ?: false
    }

    override fun createArrayOf(arrayType: String, list: Collection<Any>): Array {
        return using(session) {
            it.createArrayOf(arrayType, list)
        }
    }

    override fun createTextArray(list: Collection<String>): Array {
        return createArrayOf("text", list)
    }

    override fun createUuidArray(list: Collection<UUID>): Array {
        return createArrayOf("uuid", list)
    }

    override fun createIntArray(list: Collection<Int>): Array {
        return createArrayOf("integer", list)
    }

    override fun <T> run(query: NullableResultQueryAction<T>): T? {
        return using(session) {
            it.run(query)
        }
    }

    override fun <T> run(query: ListResultQueryAction<T>): List<T> {
        return using(session) {
            it.run(query)
        }
    }

    override fun run(query: ExecuteQueryAction): Boolean {
        return using(session) {
            it.run(query)
        }
    }

    override fun run(query: UpdateQueryAction): Int {
        return using(session) {
            it.run(query)
        }
    }

    override fun run(query: UpdateAndReturnGeneratedKeyQueryAction): Long? {
        return using(session) {
            it.run(query)
        }
    }

    override fun <T> transaction(operation: (TransactionalSession) -> T): T {
        return using(session) {
            it.transaction(operation)
        }
    }

    // Dette er basically en kopi av session.transaction metoden bare i en suspend variant
    override suspend fun <T> transactionSuspend(operation: suspend (TransactionalSession) -> T): T {
        val sess = session
        try {
            sess.connection.begin()
            sess.transactional = true
            val tx = TransactionalSession(sess.connection, sess.returnGeneratedKeys, sess.autoGeneratedKeys, sess.strict)
            val result = operation.invoke(tx)
            sess.connection.commit()
            return result
        } catch (e: Exception) {
            sess.connection.rollback()
            throw e
        } finally {
            sess.transactional = false
            sess.close()
        }
    }
}
