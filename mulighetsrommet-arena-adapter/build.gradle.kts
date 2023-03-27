plugins {
    application
    jacoco
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.flywaydb.flyway")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("no.nav.mulighetsrommet.arena.adapter.ApplicationKt")
}

ktlint {
    disabledRules.addAll("no-wildcard-imports")
}

flyway {
    url = System.getenv("DB_URL")
    user = System.getenv("DB_USERNAME")
    password = System.getenv("DB_PASSWORD")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    // Needed to use the `@OptIn` annotation for experimental features
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(projects.common.domain)
    implementation(projects.common.database)
    testImplementation(testFixtures(projects.common.database))
    implementation(projects.common.kafka)
    implementation(projects.common.ktor)
    testImplementation(testFixtures(projects.common.ktor))
    implementation(projects.common.slack)

    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.arrow.core)

    // Ktor
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    testImplementation(libs.ktor.client.mock)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authJwt)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.callLogging)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.defaultHeaders)
    implementation(libs.ktor.server.metricsMicrometer)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)

    implementation(libs.nav.common.tokenClient)
    constraints {
        implementation("net.minidev:json-smart:2.4.9") {
            because("sikkerhetshull i transitiv avhengighet rapportert via snyk")
        }
    }

    // Test
    testImplementation(libs.kotest.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.arrow)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj.db)
    testImplementation(libs.nav.mockOauth2Server)

    // Dependency injection
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Metrics
    implementation(libs.micrometer.registry.prometheus)

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.logback.logstashLogbackEncoder)
    implementation(libs.slf4j)

    implementation(libs.dbScheduler)
}
