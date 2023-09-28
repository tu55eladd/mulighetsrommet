package no.nav.mulighetsrommet.api.avtaler

import arrow.core.Either
import arrow.core.left
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.right
import no.nav.mulighetsrommet.api.repositories.AvtaleRepository
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.repositories.TiltakstypeRepository
import no.nav.mulighetsrommet.api.routes.v1.AvtaleRequest
import no.nav.mulighetsrommet.api.routes.v1.responses.ValidationError
import no.nav.mulighetsrommet.domain.Tiltakskoder
import no.nav.mulighetsrommet.domain.constants.ArenaMigrering
import no.nav.mulighetsrommet.domain.dto.Avtalestatus

class AvtaleRequestValidator(
    private val tiltakstyper: TiltakstypeRepository,
    private val avtaler: AvtaleRepository,
    private val tiltaksgjennomforinger: TiltaksgjennomforingRepository,
) {
    fun validate(request: AvtaleRequest): Either<List<ValidationError>, AvtaleRequest> = either {
        val tiltakstype = tiltakstyper.get(request.tiltakstypeId)
            ?: raise(ValidationError("tiltakstype", "Tiltakstypen finnes ikke").nel())

        val errors = buildList {
            if (!request.startDato.isBefore(request.sluttDato)) {
                add(ValidationError("startDato", "Startdato må være før sluttdato"))
            }

            if (request.navEnheter.isEmpty()) {
                add(ValidationError("navEnheter", "Minst ett NAV-kontor må være valgt"))
            }

            if (request.leverandorUnderenheter.isEmpty()) {
                add(ValidationError("leverandorUnderenheter", "Minst én underenhet til leverandøren må være valgt"))
            }

            avtaler.get(request.id)?.also { avtale ->
                if (request.opphav != avtale.opphav) {
                    add(ValidationError("opphav", "Avtalens opphav kan ikke endres"))
                }

                if (request.tiltakstypeId != avtale.tiltakstype.id) {
                    val gjennomforinger = tiltaksgjennomforinger.getAll(avtaleId = request.id)
                    if (gjennomforinger.first > 0) {
                        add(
                            ValidationError(
                                "tiltakstypeId",
                                "Kan ikke endre tiltakstype fordi det finnes gjennomføringer for avtalen",
                            ),
                        )
                    }
                }

                if (avtale.avtalestatus !in listOf(Avtalestatus.Planlagt, Avtalestatus.Aktiv)) {
                    add(
                        ValidationError(
                            "navn",
                            "Kan bare gjøre endringer når avtalen har status Planlagt eller Aktiv",
                        ),
                    )
                }

                if (avtale.avtalestatus == Avtalestatus.Aktiv) {
                    if (request.tiltakstypeId != tiltakstype.id) {
                        add(ValidationError("tiltakstypeId", "Tiltakstype kan ikke endres når avtalen er aktiv"))
                    }

                    if (request.avtaletype != avtale.avtaletype) {
                        add(ValidationError("avtaletype", "Avtaletype kan ikke endres når avtalen er aktiv"))
                    }

                    if (request.startDato != avtale.startDato) {
                        add(ValidationError("startDato", "Startdato kan ikke endres når avtalen er aktiv"))
                    }

                    if (request.sluttDato != avtale.sluttDato) {
                        add(ValidationError("sluttDato", "Sluttdato kan ikke endres når avtalen er aktiv"))
                    }

                    if (avtale.navRegion != null && request.navRegion != avtale.navRegion?.enhetsnummer) {
                        add(ValidationError("navRegion", "NAV-region kan ikke endres når avtalen er aktiv"))
                    }

                    if (request.prisOgBetalingsinformasjon != avtale.prisbetingelser) {
                        add(
                            ValidationError(
                                "prisOgBetalingsinformasjon",
                                "Pris- og betalingsinformasjon kan ikke endres når avtalen er aktiv",
                            ),
                        )
                    }

                    if (request.leverandorOrganisasjonsnummer != avtale.leverandor.organisasjonsnummer) {
                        add(
                            ValidationError(
                                "leverandorOrganisasjonsnummer",
                                "Leverandøren kan ikke endres når avtalen er aktiv",
                            ),
                        )
                    }
                }
            } ?: run {
                if (!Tiltakskoder.isTiltakMedAvtalerFraMulighetsrommet(tiltakstype.arenaKode)) {
                    add(
                        ValidationError(
                            name = "tiltakstype",
                            message = "Avtaler kan bare opprettes når de gjelder for tiltakstypene AFT eller VTA",
                        ),
                    )
                }

                if (request.opphav != ArenaMigrering.Opphav.MR_ADMIN_FLATE) {
                    add(ValidationError("opphav", "Opphav må være MR_ADMIN_FLATE"))
                }
            }
        }

        return errors.takeIf { it.isNotEmpty() }?.left() ?: request.right()
    }
}
