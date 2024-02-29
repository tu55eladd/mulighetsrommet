package no.nav.mulighetsrommet.domain

import kotlinx.serialization.Serializable

@Serializable
enum class Gruppetiltak {
    AVKLARING,
    OPPFOLGING,
    GRUPPE_ARBEIDSMARKEDSTILTAK,
    JOBBKLUBB,
    DIGITALT_OPPFOLGINGSTILTAK,
    ARBEIDSFORBEREDENDE_TRENING,
    FAG_OG_YRKESOPPLAERING,
    ARBEIDSRETTET_REHABILITERING,
    VARIG_TILRETTELAGT_ARBEID_SKJERMET,
}

object Tiltakskoder {
    /**
     * Tiltakskoder for de forhåndsgodkjente og anskaffede tiltakene, kalt "gruppetilak" (av oss i hvert fall), og som
     * skal migreres fra Arena som del av P4.
     */
    val GruppetiltakArenaKoder = listOf(
        "ARBFORB",
        "ARBRRHDAG",
        "AVKLARAG",
        "DIGIOPPARB",
        "GRUFAGYRKE",
        "GRUPPEAMO",
        "INDJOBSTOT",
        "INDOPPFAG",
        "IPSUNG",
        "JOBBK",
        "UTVAOONAV",
        "VASV",
    )

    /**
     * Tiltakskoder for de gruppetiltak som er i egen regi, og som administreres i Sanity ikke i admin-flate
     */
    val EgenRegiTiltak = listOf(
        "INDJOBSTOT",
        "IPSUNG",
        "UTVAOONAV",
    )

    /**
     * Tiltakskoder som, enn så lenge, blir antatt å ha en felles oppstartsdato for alle deltakere.
     * Disse har blitt referert til som "kurs" av komet.
     */
    val TiltakMedFellesOppstart = listOf(
        "GRUPPEAMO",
        "JOBBK",
        "GRUFAGYRKE",
    )

    /**
     * Tiltakskoder der Komet har tatt eierskap til deltakelsene.
     */
    val AmtTiltak = listOf(
        "ARBFORB",
        "ARBRRHDAG",
        "AVKLARAG",
        "DIGIOPPARB",
        "GRUFAGYRKE",
        "GRUPPEAMO",
        "INDOPPFAG",
        "JOBBK",
        "VASV",
    )

    val TiltakMedAvtalerFraMulighetsrommet = listOf(
        "ARBFORB",
        "VASV",
    )

    fun isGruppetiltak(arenaKode: String): Boolean {
        return arenaKode in GruppetiltakArenaKoder
    }

    fun isEgenRegiTiltak(arenaKode: String): Boolean {
        return arenaKode in EgenRegiTiltak
    }

    fun isKursTiltak(arenaKode: String): Boolean {
        return arenaKode in TiltakMedFellesOppstart
    }

    fun isAmtTiltak(arenaKode: String): Boolean {
        return arenaKode in AmtTiltak
    }

    fun isTiltakMedAvtalerFraMulighetsrommet(arenaKode: String): Boolean {
        return arenaKode in TiltakMedAvtalerFraMulighetsrommet
    }
}
