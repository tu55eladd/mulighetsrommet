package no.nav.mulighetsrommet.database.kotest.extensions

import no.nav.mulighetsrommet.database.FlywayDatabaseAdapter
import no.nav.mulighetsrommet.database.Password

fun createDatabaseTestSchema(
    name: String,
    port: Int,
    host: String = "localhost",
    user: String = "valp",
    password: Password = Password("valp"),
): FlywayDatabaseAdapter.Config {
    val schema = "test-schema"
    return FlywayDatabaseAdapter.Config(
        host,
        port,
        name,
        schema,
        user,
        password,
        2,
        migrationConfig = FlywayDatabaseAdapter.MigrationConfig(cleanDisabled = false),
    )
}
