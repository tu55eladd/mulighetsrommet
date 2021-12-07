package no.nav.amt_informasjon_api

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.routing.*
import kotlinx.coroutines.launch
import no.nav.amt_informasjon_api.kafka.KafkaFactory
import no.nav.amt_informasjon_api.plugins.*
import no.nav.amt_informasjon_api.routes.devRoutes
import no.nav.amt_informasjon_api.routes.healthRoutes
import no.nav.amt_informasjon_api.routes.tiltaksgjennomforingRoutes
import no.nav.amt_informasjon_api.routes.tiltaksvariantRoutes
import no.nav.amt_informasjon_api.services.TiltaksgjennomforingService
import no.nav.amt_informasjon_api.services.TiltaksvariantService

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    // TODO: Fiks litt bedre config-oppsett for hele appen, sett i app context isteden.
    val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val enableKafka = appConfig.property("ktor.kafka.enable").getString().toBoolean()
    val kafka: KafkaFactory

    configureRouting()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()

    val tiltaksvariantService = TiltaksvariantService()
    val tiltaksgjennomforingService = TiltaksgjennomforingService()

    routing {
        devRoutes()
        healthRoutes()
        tiltaksvariantRoutes(tiltaksvariantService, tiltaksgjennomforingService)
        tiltaksgjennomforingRoutes(tiltaksgjennomforingService)
    }

    // TODO: Lag noe som er litt mer robust. Kun for å få deployet.
    if (enableKafka) {
        kafka = KafkaFactory()
        val kafkaConsumers = launch {
            kafka.consumeTiltaksgjennomforingEventsFromArena()
        }
        kafkaConsumers.start()
        environment.monitor.subscribe(ApplicationStopped) {
            println("Shutting down")
//            kafka.shutdown()
        }
    }
}