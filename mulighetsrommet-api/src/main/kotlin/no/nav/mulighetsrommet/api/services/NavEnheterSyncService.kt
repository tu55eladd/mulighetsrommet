package no.nav.mulighetsrommet.api.services

import io.ktor.http.*
import no.nav.mulighetsrommet.api.clients.norg2.*
import no.nav.mulighetsrommet.api.clients.sanity.SanityClient
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetDbo
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetStatus
import no.nav.mulighetsrommet.api.domain.dto.EnhetSlug
import no.nav.mulighetsrommet.api.domain.dto.FylkeRef
import no.nav.mulighetsrommet.api.domain.dto.Mutation
import no.nav.mulighetsrommet.api.domain.dto.SanityEnhet
import no.nav.mulighetsrommet.api.repositories.NavEnhetRepository
import no.nav.mulighetsrommet.api.utils.NavEnhetUtils
import no.nav.mulighetsrommet.slack.SlackNotifier
import org.slf4j.LoggerFactory

class NavEnheterSyncService(
    private val norg2Client: Norg2Client,
    private val sanityClient: SanityClient,
    private val enhetRepository: NavEnhetRepository,
    private val slackNotifier: SlackNotifier,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun synkroniserEnheter() {
        val enheter = norg2Client.hentEnheter()

        logger.info("Hentet ${enheter.size} enheter fra NORG2")

        lagreEnheter(enheter)

        val enheterToSanity = utledEnheterTilSanity(enheter)
        lagreEnheterTilSanity(enheterToSanity)
    }

    private fun lagreEnheter(enheter: List<Norg2Response>) {
        logger.info("Lagrer ${enheter.size} enheter til database")

        enheter.forEach {
            enhetRepository.upsert(
                NavEnhetDbo(
                    navn = it.enhet.navn,
                    enhetsnummer = it.enhet.enhetNr,
                    status = NavEnhetStatus.valueOf(it.enhet.status.name),
                    type = Norg2Type.valueOf(it.enhet.type.name),
                    overordnetEnhet = it.overordnetEnhet ?: tryResolveOverordnetEnhet(it.enhet),
                ),
            )
        }
    }

    fun utledEnheterTilSanity(enheter: List<Norg2Response>): List<SanityEnhet> {
        val relevanteEnheterMedJustertOverordnetEnhet = enheter
            .filter { isRelevantEnhetForSanity(it) }
            .map {
                val overordnetEnhet = it.overordnetEnhet ?: tryResolveOverordnetEnhet(it.enhet)
                it.copy(overordnetEnhet = overordnetEnhet)
            }

        val fylker = relevanteEnheterMedJustertOverordnetEnhet
            .filter { it.enhet.type == Norg2Type.FYLKE }

        return fylker.flatMap { fylke ->
            val underliggendeEnheter = relevanteEnheterMedJustertOverordnetEnhet
                .filter { it.overordnetEnhet == fylke.enhet.enhetNr }
                .map { toSanityEnhet(it.enhet, fylke.enhet) }

            listOf(toSanityEnhet(fylke.enhet)) + underliggendeEnheter
        }
    }

    private fun isRelevantEnhetForSanity(it: Norg2Response): Boolean {
        return NavEnhetUtils.isRelevantEnhetStatus(it.enhet.status) && NavEnhetUtils.isRelevantEnhetType(it.enhet.type)
    }

    suspend fun lagreEnheterTilSanity(sanityEnheter: List<SanityEnhet>) {
        logger.info("Oppdaterer Sanity-enheter - Antall: ${sanityEnheter.size}")
        val mutations = sanityEnheter.map { Mutation(createOrReplace = it) }

        val response = sanityClient.mutate(mutations)

        if (response.status.value != HttpStatusCode.OK.value) {
            logger.error("Klarte ikke oppdatere enheter fra NORG til Sanity: {}", response.status)
            slackNotifier.sendMessage("Klarte ikke oppdatere enheter fra NORG til Sanity. Statuskode: ${response.status.value}. Dette må sees på av en utvikler.")
        } else {
            logger.info("Oppdaterte enheter fra NORG til Sanity.")
        }
    }

    private fun toSanityEnhet(enhet: Norg2EnhetDto, fylke: Norg2EnhetDto? = null): SanityEnhet {
        var fylkeTilEnhet: FylkeRef? = null

        if (fylke != null) {
            fylkeTilEnhet = FylkeRef(
                _type = "reference",
                _ref = NavEnhetUtils.toEnhetId(fylke),
                _key = fylke.enhetNr,
            )
        }

        return SanityEnhet(
            _id = NavEnhetUtils.toEnhetId(enhet),
            _type = "enhet",
            navn = enhet.navn,
            nummer = EnhetSlug(
                _type = "slug",
                current = enhet.enhetNr,
            ),
            type = NavEnhetUtils.toType(enhet.type.name),
            status = NavEnhetUtils.toStatus(enhet.status.name),
            fylke = fylkeTilEnhet,
        )
    }

    private fun tryResolveOverordnetEnhet(enhet: Norg2EnhetDto): String? {
        if (!NavEnhetUtils.isRelevantEnhetStatus(enhet.status) || !listOf(Norg2Type.ALS, Norg2Type.TILTAK).contains(
                enhet.type,
            )
        ) {
            return null
        }

        val spesialEnheterTilFylkeMap = mapOf(
            "1291" to "1200", // Vestland
            "0291" to "0200", // Øst-Viken
            "1591" to "1500", // Møre og Romsdal,
            "1891" to "1800", // Nordland
            "0491" to "0400", // Innlandet
            "0691" to "0600", // Vest-Viken,
            "0891" to "0800", // Vestfold og Telemark
            "1091" to "1000", // Agder,
            "1991" to "1900", // Troms og Finnmark
            "5772" to "5700", // Trøndelag,
            "0391" to "0300", // Oslo
            "1191" to "1100", // Rogaland
            "1287" to "1200", // NAV Tiltak Vestland
            "1987" to "1900", // NAV Tiltak Troms og Finnmark,
            "0287" to "0200", // NAV Tiltak Øst-Viken
            "0387" to "0300", // NAV Tiltak Oslo
            "0587" to "0400", // NAV Tiltak Innlandet,
            "0687" to "0600", // NAV Forvaltningstjenester Vest-Viken
            "1087" to "1000", // NAV Tiltak Agder
            "1187" to "1100", // NAV Tiltak Rogaland
            "1194" to "1100", // NAV Marked Sør-Rogaland
            "1193" to "1100", // NAV Marked Nord-Rogaland
            "5771" to "5700", // NAV Tiltak Trøndelag
        )

        val fantFylke = spesialEnheterTilFylkeMap[enhet.enhetNr]
        if (fantFylke == null) {
            slackNotifier.sendMessage("Fant ikke fylke for spesialenhet med enhetsnummer: ${enhet.enhetNr}. En utvikler må sjekke om enheten skal mappe til et fylke.")
            return null
        }
        return fantFylke
    }
}
