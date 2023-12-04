package no.nav.mulighetsrommet.database.kotest.extensions

import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import kotliquery.queryOf
import no.nav.mulighetsrommet.database.FlywayDatabaseAdapter
import org.assertj.db.api.Assertions
import org.assertj.db.api.TableAssert
import org.assertj.db.type.Table

class FlywayDatabaseTestListener(private val config: FlywayDatabaseAdapter.Config) :
    BeforeSpecListener,
    AfterSpecListener,
    BeforeEachListener,
    AfterEachListener {
    private var delegate: FlywayDatabaseAdapter? = null

    private var beforeEachFlywayMigration: FlywayDatabaseAdapter? = null

    val db: FlywayDatabaseAdapter
        get() {
            return delegate ?: throw RuntimeException("Database has not yet been initialized")
        }

    override suspend fun beforeSpec(spec: Spec) {
        // It's not optimal to force a sequential test order, but since tests (for now) all share the same database
        // instance they can't be run in parallel
        spec.testOrder = TestCaseOrder.Sequential

        delegate = FlywayDatabaseAdapter(config, slackNotifier = null)
    }

    override suspend fun afterSpec(spec: Spec) {
        delegate?.truncateAll()
        delegate?.close()
    }

    // Initialiserer ny connection pool per test pga potensielle caching issues mellom tester
    // https://github.com/flyway/flyway/issues/2323#issuecomment-804495818
    override suspend fun beforeEach(testCase: TestCase) {
        beforeEachFlywayMigration = FlywayDatabaseAdapter(config, slackNotifier = null)
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        beforeEachFlywayMigration?.close()
    }

    fun assertThat(tableName: String): TableAssert {
        val table = Table(db.getDatasource(), tableName)
        return Assertions.assertThat(table)
    }
}

fun FlywayDatabaseAdapter.truncateAll() {
    val tableNames =
        queryOf("SELECT table_name FROM information_schema.tables WHERE table_schema='${config.schema}' AND table_type='BASE TABLE'")
            .map { it.string("table_name") }
            .asList
            .let { run(it) }
    tableNames.forEach {
        run(queryOf("truncate table $it restart identity cascade").asExecute)
    }
}
