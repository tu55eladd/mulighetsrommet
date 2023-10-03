package no.nav.mulighetsrommet.api.services

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.right
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import no.nav.mulighetsrommet.api.clients.sanity.SanityClient
import no.nav.mulighetsrommet.api.domain.dbo.NavAnsattDbo
import no.nav.mulighetsrommet.api.domain.dbo.NavAnsattRolle
import no.nav.mulighetsrommet.api.domain.dto.Delete
import no.nav.mulighetsrommet.api.domain.dto.Mutation
import no.nav.mulighetsrommet.api.domain.dto.NavAnsattDto
import no.nav.mulighetsrommet.api.domain.dto.SanityResponse
import no.nav.mulighetsrommet.api.repositories.NavAnsattRepository
import no.nav.mulighetsrommet.api.utils.NavAnsattFilter
import no.nav.mulighetsrommet.database.utils.DatabaseOperationError
import no.nav.mulighetsrommet.database.utils.getOrThrow
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class NavAnsattService(
    private val microsoftGraphService: MicrosoftGraphService,
    private val ansatte: NavAnsattRepository,
    private val roles: List<AdGruppeNavAnsattRolleMapping>,
    private val sanityClient: SanityClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun getOrSynchronizeNavAnsatt(azureId: UUID): NavAnsattDto {
        return ansatte.getByAzureId(azureId)
            .flatMap {
                it?.right() ?: run {
                    logger.info("Fant ikke NavAnsatt for azureId=$azureId i databasen, forsøker Azure AD i stedet")
                    val ansatt = getNavAnsattFromAzure(azureId)
                    ansatte.upsert(NavAnsattDbo.fromNavAnsattDto(ansatt)).map { ansatt }
                }
            }
            .getOrThrow()
    }

    fun getNavAnsatte(filter: NavAnsattFilter): List<NavAnsattDto> {
        return ansatte.getAll(roller = filter.roller).getOrThrow()
    }

    suspend fun getNavAnsattFromAzure(azureId: UUID): NavAnsattDto {
        val rolesDirectory = roles.associateBy { it.adGruppeId }

        val roller = microsoftGraphService.getNavAnsattAdGrupper(azureId)
            .filter { rolesDirectory.containsKey(it.id) }
            .map { rolesDirectory.getValue(it.id).rolle }
            .toSet()

        if (roller.isEmpty()) {
            logger.info("Ansatt med azureId=$azureId har ingen av rollene $roles")
            throw IllegalStateException("Ansatt med azureId=$azureId har ingen av de påkrevde rollene")
        }

        val ansatt = microsoftGraphService.getNavAnsatt(azureId)
        return NavAnsattDto.fromAzureAdNavAnsatt(ansatt, roller)
    }

    suspend fun getNavAnsatteFromAzure(): List<NavAnsattDto> {
        return roles
            .flatMap {
                val members = microsoftGraphService.getNavAnsatteInGroup(it.adGruppeId)
                logger.info("Fant ${members.size} i AD gruppe id=${it.adGruppeId}")
                members.map { ansatt ->
                    NavAnsattDto.fromAzureAdNavAnsatt(ansatt, setOf(it.rolle))
                }
            }
            .groupBy { it.navIdent }
            .map { (_, value) ->
                value.reduce { a1, a2 ->
                    a1.copy(roller = a1.roller + a2.roller)
                }
            }
    }

    suspend fun synchronizeNavAnsatte(
        today: LocalDate,
        deletionDate: LocalDate,
    ): Either<DatabaseOperationError, Unit> = either {
        val ansatteToUpsert = getNavAnsatteFromAzure()

        logger.info("Oppdaterer ${ansatteToUpsert.size} NavAnsatt fra Azure")
        ansatteToUpsert.forEach { ansatt ->
            ansatte.upsert(NavAnsattDbo.fromNavAnsattDto(ansatt)).bind()
            upsertSanityAnsatt(ansatt)
        }

        val ansatteAzureIds = ansatteToUpsert.map { it.azureId }
        val ansatteToScheduleForDeletion = ansatte.getAll()
            .map { it.filter { ansatt -> ansatt.azureId !in ansatteAzureIds && ansatt.skalSlettesDato == null } }
            .bind()
        ansatteToScheduleForDeletion.forEach { ansatt ->
            logger.info("Oppdaterer NavAnsatt med dato for sletting azureId=${ansatt.azureId} dato=$deletionDate")
            val ansattToDelete = ansatt.copy(roller = emptySet(), skalSlettesDato = deletionDate)
            ansatte.upsert(NavAnsattDbo.fromNavAnsattDto(ansattToDelete)).bind()
        }

        val ansatteToDelete = ansatte.getAll(skalSlettesDatoLte = today).bind()
        ansatteToDelete.forEach { ansatt ->
            logger.info("Sletter NavAnsatt fordi vi har passert dato for sletting azureId=${ansatt.azureId} dato=${ansatt.skalSlettesDato}")
            ansatte.deleteByAzureId(ansatt.azureId).bind()
            deleteSanityAnsatt(ansatt)
        }
    }

    private suspend fun deleteSanityAnsatt(ansatt: NavAnsattDto) {
        val queryResponse = sanityClient.query(
            """
            *[_type == "navKontaktperson" && lower(epost) == lower(${ansatt.epost}) || _type == "redaktor" && lower(epost.current) == lower(${ansatt.epost})]._id
            """.trimIndent(),
        )

        val ider = when (queryResponse) {
            is SanityResponse.Result -> queryResponse.decode<List<String>>()
            is SanityResponse.Error -> throw Exception("Klarte ikke hente ut id'er til sletting fra Sanity: ${queryResponse.error}")
        }

        val result = sanityClient.mutate(mutations = ider.map { Mutation(Delete(it)) })

        if (result.status != HttpStatusCode.OK) {
            throw Exception("Klarte ikke slette Sanity-dokument: ${result.bodyAsText()}")
        }
    }

    suspend fun upsertSanityAnsatt(ansatt: NavAnsattDto) {
        if (ansatt.roller.contains(NavAnsattRolle.KONTAKTPERSON)) {
            upsertKontaktperson(ansatt)
        }

        if (ansatt.roller.contains(NavAnsattRolle.BETABRUKER)) {
            upsertRedaktor(ansatt)
        }
    }

    private suspend fun upsertKontaktperson(ansatt: NavAnsattDto) {
        val queryResponse = sanityClient.query(
            """
            *[_type == "navKontaktperson" && lower(epost) == lower(${ansatt.epost})][0]
            """.trimIndent(),
        )

        val sanityId = when (queryResponse) {
            is SanityResponse.Result -> queryResponse.decode<SanityNavKontaktperson?>()?._id
                ?: UUID.randomUUID()

            is SanityResponse.Error -> throw Exception("Klarte ikke hente ut kontaktperson fra Sanity: ${queryResponse.error}")
        }

        val sanityPatch = SanityNavKontaktperson(
            _id = sanityId.toString(),
            _type = "navKontaktperson",
            enhet = "${ansatt.hovedenhet.enhetsnummer} ${ansatt.hovedenhet.navn}",
            telefonnummer = ansatt.mobilnummer,
            epost = ansatt.epost,
        )

        val response = sanityClient.mutate(
            listOf(
                Mutation(createOrReplace = sanityPatch),
            ),
        )

        if (response.status != HttpStatusCode.OK) {
            throw Exception("Klarte ikke upserte kontaktperson i sanity: ${response.bodyAsText()}")
        } else {
            logger.info("Oppdaterte kontaktperson i Sanity med id: $sanityId")
        }
    }

    private suspend fun upsertRedaktor(ansatt: NavAnsattDto) {
        val queryResponse = sanityClient.query(
            """
            *[_type == "redaktor" && lower(epost.current) == lower(${ansatt.epost})][0]
            """.trimIndent(),
        )

        val sanityId = when (queryResponse) {
            is SanityResponse.Result -> queryResponse.decode<SanityRedaktor?>()?._id
                ?: UUID.randomUUID()

            is SanityResponse.Error -> throw Exception("Klarte ikke hente ut redaktør fra Sanity: ${queryResponse.error}")
        }

        val sanityPatch = SanityRedaktor(
            _id = sanityId.toString(),
            _type = "redaktor",
            enhet = "${ansatt.hovedenhet.enhetsnummer} ${ansatt.hovedenhet.navn}",
            epost = Slug(
                _type = "slug",
                current = ansatt.epost,
            ),
        )

        val response = sanityClient.mutate(
            listOf(
                Mutation(createOrReplace = sanityPatch),
            ),
        )

        if (response.status != HttpStatusCode.OK) {
            throw Exception("Klarte ikke upserte redaktør i sanity: ${response.bodyAsText()}")
        } else {
            logger.info("Oppdaterte redaktør i Sanity med id: $sanityId")
        }
    }
}

data class AdGruppeNavAnsattRolleMapping(
    val adGruppeId: UUID,
    val rolle: NavAnsattRolle,
)

@Serializable
data class SanityNavKontaktperson(
    val _id: String,
    val _type: String,
    val enhet: String,
    val telefonnummer: String?,
    val epost: String,
)

@Serializable
data class SanityRedaktor(
    val _id: String,
    val _type: String,
    val enhet: String,
    val epost: Slug,
)

@Serializable
data class Slug(
    val _type: String,
    val current: String,
)
