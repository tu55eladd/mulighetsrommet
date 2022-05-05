group = "no.nav.mulighetsrommet"
version = "0.0.1"

plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.flywaydb.flyway") version "8.5.5" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}
