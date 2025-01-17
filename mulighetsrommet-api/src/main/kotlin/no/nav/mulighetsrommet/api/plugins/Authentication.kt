package no.nav.mulighetsrommet.api.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import no.nav.mulighetsrommet.api.AuthConfig
import no.nav.mulighetsrommet.api.domain.dbo.NavAnsattRolle
import no.nav.mulighetsrommet.domain.dto.NavIdent
import no.nav.mulighetsrommet.ktor.exception.StatusException
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit

enum class AuthProvider {
    AZURE_AD_NAV_IDENT,
    AZURE_AD_TEAM_MULIGHETSROMMET,
    AZURE_AD_DEFAULT_APP,
    AZURE_AD_TILTAKSGJENNOMFORING_APP,
    AZURE_AD_AVTALER_SKRIV,
    AZURE_AD_TILTAKSJENNOMFORINGER_SKRIV,
    AZURE_AD_TILTAKSADMINISTRASJON_GENERELL,
    AZURE_AD_OKONOMI_BESLUTTER,
}

object AppRoles {
    const val ACCESS_AS_APPLICATION = "access_as_application"
    const val READ_TILTAKSGJENNOMFORING = "tiltaksgjennomforing-read"
}

/**
 * Utility that requires all [AuthProvider]'s specified in [configurations] to authenticate the request.
 */
fun Route.authenticate(
    vararg configurations: AuthProvider,
    build: Route.() -> Unit,
): Route {
    return authenticate(
        configurations = configurations.map { it.name }.toTypedArray(),
        strategy = AuthenticationStrategy.Required,
        build = build,
    )
}

/**
 * Gets a NAVident from the underlying [JWTPrincipal], or throws a [StatusException]
 * if the claim is not available.
 */
fun <T : Any> PipelineContext<T, ApplicationCall>.getNavIdent(): NavIdent {
    return call.principal<JWTPrincipal>()?.get("NAVident")?.let { NavIdent(it) } ?: throw StatusException(
        HttpStatusCode.Forbidden,
        "NAVident mangler i JWTPrincipal",
    )
}

/**
 * Gets a NavAnsattAzureId from the underlying [JWTPrincipal], or throws a [StatusException]
 * if the claim is not available.
 */
fun <T : Any> PipelineContext<T, ApplicationCall>.getNavAnsattAzureId(): UUID {
    return call.principal<JWTPrincipal>()?.get("oid")?.let { UUID.fromString(it) } ?: throw StatusException(
        HttpStatusCode.Forbidden,
        "NavAnsattAzureId mangler i JWTPrincipal",
    )
}

/**
 * Utility to implement a JWT [Authentication] provider with its named derived from the [authProvider] paramater.
 */
private fun AuthenticationConfig.jwt(
    authProvider: AuthProvider,
    configure: JWTAuthenticationProvider.Config.() -> Unit,
) = jwt(authProvider.name, configure)

fun Application.configureAuthentication(
    auth: AuthConfig,
) {
    val (azure) = auth

    val jwkProvider = JwkProviderBuilder(URI(azure.jwksUri).toURL()).cached(5, 12, TimeUnit.HOURS).build()

    fun hasApplicationRoles(credentials: JWTCredential, vararg requiredRoles: String): Boolean {
        val roles = credentials.getListClaim("roles", String::class)
        return requiredRoles.all { it in roles }
    }

    fun hasNavAnsattRoles(credentials: JWTCredential, vararg requiredRoles: NavAnsattRolle): Boolean {
        val navAnsattGroups = credentials.getListClaim("groups", UUID::class)
        return requiredRoles.all { requiredRole ->
            auth.roles.any { (groupId, role) -> role == requiredRole && groupId in navAnsattGroups }
        }
    }

    install(Authentication) {
        jwt(AuthProvider.AZURE_AD_TEAM_MULIGHETSROMMET) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                if (!hasNavAnsattRoles(credentials, NavAnsattRolle.TEAM_MULIGHETSROMMET)) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_TILTAKSADMINISTRASJON_GENERELL) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                if (!hasNavAnsattRoles(
                        credentials,
                        NavAnsattRolle.TILTAKADMINISTRASJON_GENERELL,
                    )
                ) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_AVTALER_SKRIV) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                if (!hasNavAnsattRoles(
                        credentials,
                        NavAnsattRolle.AVTALER_SKRIV,
                        NavAnsattRolle.TILTAKADMINISTRASJON_GENERELL,
                    )
                ) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_TILTAKSJENNOMFORINGER_SKRIV) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                if (!hasNavAnsattRoles(
                        credentials,
                        NavAnsattRolle.TILTAKSGJENNOMFORINGER_SKRIV,
                        NavAnsattRolle.TILTAKADMINISTRASJON_GENERELL,
                    )
                ) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_OKONOMI_BESLUTTER) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                if (!hasNavAnsattRoles(
                        credentials,
                        NavAnsattRolle.OKONOMI_BESLUTTER,
                        NavAnsattRolle.TILTAKADMINISTRASJON_GENERELL,
                    )
                ) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_NAV_IDENT) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                credentials["NAVident"] ?: return@validate null

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_DEFAULT_APP) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                if (!hasApplicationRoles(credentials, AppRoles.ACCESS_AS_APPLICATION)) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }

        jwt(AuthProvider.AZURE_AD_TILTAKSGJENNOMFORING_APP) {
            verifier(jwkProvider, azure.issuer) {
                withAudience(azure.audience)
            }

            validate { credentials ->
                if (!hasApplicationRoles(
                        credentials,
                        AppRoles.ACCESS_AS_APPLICATION,
                        AppRoles.READ_TILTAKSGJENNOMFORING,
                    )
                ) {
                    return@validate null
                }

                JWTPrincipal(credentials.payload)
            }
        }
    }
}
