package no.nav.mulighetsrommet.api.plugins

import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import io.ktor.server.application.*
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.AzureAdOnBehalfOfTokenClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.mulighetsrommet.api.AppConfig
import no.nav.mulighetsrommet.api.clients.arena.VeilarbarenaClient
import no.nav.mulighetsrommet.api.clients.arena.VeilarbarenaClientImpl
import no.nav.mulighetsrommet.api.clients.arena_ords_proxy.ArenaOrdsProxyClient
import no.nav.mulighetsrommet.api.clients.arena_ords_proxy.ArenaOrdsProxyClientImpl
import no.nav.mulighetsrommet.api.clients.dialog.VeilarbdialogClient
import no.nav.mulighetsrommet.api.clients.dialog.VeilarbdialogClientImpl
import no.nav.mulighetsrommet.api.clients.enhetsregister.AmtEnhetsregisterClient
import no.nav.mulighetsrommet.api.clients.enhetsregister.AmtEnhetsregisterClientImpl
import no.nav.mulighetsrommet.api.clients.oppfolging.VeilarboppfolgingClient
import no.nav.mulighetsrommet.api.clients.oppfolging.VeilarboppfolgingClientImpl
import no.nav.mulighetsrommet.api.clients.person.VeilarbpersonClient
import no.nav.mulighetsrommet.api.clients.person.VeilarbpersonClientImpl
import no.nav.mulighetsrommet.api.clients.vedtak.VeilarbvedtaksstotteClient
import no.nav.mulighetsrommet.api.clients.vedtak.VeilarbvedtaksstotteClientImpl
import no.nav.mulighetsrommet.api.clients.veileder.VeilarbveilederClient
import no.nav.mulighetsrommet.api.clients.veileder.VeilarbveilederClientImpl
import no.nav.mulighetsrommet.api.repositories.ArenaRepository
import no.nav.mulighetsrommet.api.services.*
import no.nav.mulighetsrommet.database.Database
import no.nav.mulighetsrommet.database.DatabaseConfig
import no.nav.mulighetsrommet.database.FlywayDatabaseAdapter
import no.nav.poao_tilgang.client.PoaoTilgangHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

fun Application.configureDependencyInjection(appConfig: AppConfig) {
    install(Koin) {
        SLF4JLogger()

        modules(
            db(appConfig.database),
            repositories(),
            services(
                appConfig,
                veilarbvedsstotte(appConfig),
                veilarboppfolging(appConfig),
                veilarbperson(appConfig),
                veilarbdialog(appConfig),
                veilarbveileder(appConfig),
                veilarbarena(appConfig),
                arenaordsproxy(appConfig),
                amtenhetsregister(appConfig)
            )
        )
    }
}

private fun db(databaseConfig: DatabaseConfig): Module {
    return module(createdAtStart = true) {
        single<Database> { FlywayDatabaseAdapter(databaseConfig) }
    }
}

private fun veilarbvedsstotte(config: AppConfig): VeilarbvedtaksstotteClient {
    return VeilarbvedtaksstotteClientImpl(
        config.veilarbvedtaksstotteConfig.url,
        tokenClientProvider(config),
        config.veilarbvedtaksstotteConfig.scope,
        config.veilarbvedtaksstotteConfig.httpClient
    )
}

private fun veilarboppfolging(config: AppConfig): VeilarboppfolgingClient {
    return VeilarboppfolgingClientImpl(
        config.veilarboppfolgingConfig.url,
        tokenClientProvider(config),
        config.veilarboppfolgingConfig.scope,
        config.veilarboppfolgingConfig.httpClient
    )
}

private fun veilarbperson(config: AppConfig): VeilarbpersonClient {
    return VeilarbpersonClientImpl(
        config.veilarbpersonConfig.url,
        tokenClientProvider(config),
        config.veilarbpersonConfig.scope,
        config.veilarbpersonConfig.httpClient
    )
}

private fun veilarbdialog(config: AppConfig): VeilarbdialogClient {
    return VeilarbdialogClientImpl(
        config.veilarbdialogConfig.url,
        tokenClientProvider(config),
        config.veilarbdialogConfig.scope,
        config.veilarbdialogConfig.httpClient
    )
}

private fun veilarbveileder(config: AppConfig): VeilarbveilederClient {
    return VeilarbveilederClientImpl(
        config.veilarbveilederConfig.url,
        tokenClientProvider(config),
        config.veilarbveilederConfig.scope,
        config.veilarbveilederConfig.httpClient
    )
}

private fun veilarbarena(config: AppConfig): VeilarbarenaClient {
    return VeilarbarenaClientImpl(
        config.poaoGcpProxy.url,
        tokenClientProviderForMachineToMachine(config),
        tokenClientProvider(config),
        config.veilarbarenaConfig.scope,
        config.poaoGcpProxy.scope,
        config.veilarbarenaConfig.httpClient
    )
}

private fun arenaordsproxy(config: AppConfig): ArenaOrdsProxyClient {
    return ArenaOrdsProxyClientImpl(
        baseUrl = config.arenaOrdsProxy.url,
        machineToMachineTokenClient = tokenClientProviderForMachineToMachine(config),
        scope = config.arenaOrdsProxy.scope
    )
}

private fun amtenhetsregister(config: AppConfig): AmtEnhetsregisterClient {
    return AmtEnhetsregisterClientImpl(
        baseUrl = config.amtEnhetsregister.url,
        machineToMachineTokenClient = tokenClientProviderForMachineToMachine(config),
        scope = config.amtEnhetsregister.scope
    )
}

private fun tokenClientProvider(config: AppConfig): AzureAdOnBehalfOfTokenClient {
    return when (erLokalUtvikling()) {
        true -> AzureAdTokenClientBuilder.builder()
            .withClientId(config.auth.azure.audience)
            .withPrivateJwk(createRSAKeyForLokalUtvikling("azure").toJSONString())
            .withTokenEndpointUrl(config.auth.azure.tokenEndpointUrl)
            .buildOnBehalfOfTokenClient()
        false -> AzureAdTokenClientBuilder.builder().withNaisDefaults().buildOnBehalfOfTokenClient()
    }
}

private fun tokenClientProviderForMachineToMachine(config: AppConfig): MachineToMachineTokenClient {
    return when (erLokalUtvikling()) {
        true -> AzureAdTokenClientBuilder.builder()
            .withClientId(config.auth.azure.audience)
            .withPrivateJwk(createRSAKeyForLokalUtvikling("azure").toJSONString())
            .withTokenEndpointUrl(config.auth.azure.tokenEndpointUrl)
            .buildMachineToMachineTokenClient()
        false -> AzureAdTokenClientBuilder.builder().withNaisDefaults().buildMachineToMachineTokenClient()
    }
}

private fun repositories() = module {
    single { ArenaRepository(get()) }
}

private fun services(
    appConfig: AppConfig,
    veilarbvedsstotte: VeilarbvedtaksstotteClient,
    veilarboppfolging: VeilarboppfolgingClient,
    veilarbpersonClient: VeilarbpersonClient,
    veilarbdialogClient: VeilarbdialogClient,
    veilarbveilerClient: VeilarbveilederClient,
    veilarbarenaClient: VeilarbarenaClient,
    arenaOrdsProxyClient: ArenaOrdsProxyClient,
    amtEnhetsregisterClient: AmtEnhetsregisterClient,
) = module {
    val m2mTokenProvider = tokenClientProviderForMachineToMachine(appConfig)

    single { ArenaService(get()) }
    single { TiltaksgjennomforingService(get()) }
    single { TiltakstypeService(get()) }
    single { InnsatsgruppeService(get()) }
    single { HistorikkService(get(), veilarbarenaClient, get()) }
    single { SanityService(appConfig.sanity, get()) }
    single { ArrangorService(arenaOrdsProxyClient, amtEnhetsregisterClient) }
    single {
        BrukerService(
            veilarboppfolgingClient = veilarboppfolging,
            veilarbvedtaksstotteClient = veilarbvedsstotte,
            veilarbpersonClient = veilarbpersonClient
        )
    }
    single { DialogService(veilarbdialogClient) }
    single {
        VeilederService(
            veilarbveilederClient = veilarbveilerClient
        )
    }
    single {
        val poaoTilgangClient = PoaoTilgangHttpClient(
            appConfig.poaoTilgang.url,
            { m2mTokenProvider.createMachineToMachineToken(appConfig.poaoTilgang.scope) }
        )
        PoaoTilgangService(poaoTilgangClient)
    }
    single { DelMedBrukerService(get()) }
}

private fun erLokalUtvikling(): Boolean {
    return System.getenv("NAIS_CLUSTER_NAME") == null
}

private fun createRSAKeyForLokalUtvikling(keyID: String): RSAKey = KeyPairGenerator
    .getInstance("RSA").let {
        it.initialize(2048)
        it.generateKeyPair()
    }.let {
        RSAKey.Builder(it.public as RSAPublicKey)
            .privateKey(it.private as RSAPrivateKey)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(keyID)
            .build()
    }
