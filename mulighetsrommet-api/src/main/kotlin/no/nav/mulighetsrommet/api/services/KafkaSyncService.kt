package no.nav.mulighetsrommet.api.services

import no.nav.mulighetsrommet.api.producers.TiltaksgjennomforingKafkaProducer
import no.nav.mulighetsrommet.api.repositories.TiltaksgjennomforingRepository
import no.nav.mulighetsrommet.api.utils.PaginationParams
import no.nav.mulighetsrommet.domain.dbo.Avslutningsstatus
import no.nav.mulighetsrommet.domain.dto.TiltaksgjennomforingDto
import org.slf4j.LoggerFactory
import java.time.LocalDate

class KafkaSyncService(
    private val tiltaksgjennomforingRepository: TiltaksgjennomforingRepository,
    private val tiltaksgjennomforingKafkaProducer: TiltaksgjennomforingKafkaProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun oppdaterTiltaksgjennomforingsstatus(today: LocalDate, lastSuccessDate: LocalDate) {
        var offset = 1
        var count = 0

        logger.info("Oppdaterer statuser for gjennomføringer med start eller sluttdato mellom $lastSuccessDate og $today")
        do {
            val tiltaksgjennomforinger = tiltaksgjennomforingRepository.getAllByDateIntervalAndAvslutningsstatus(
                dateIntervalStart = lastSuccessDate,
                dateIntervalEnd = today,
                avslutningsstatus = Avslutningsstatus.IKKE_AVSLUTTET,
                pagination = PaginationParams(nullablePage = offset, nullableLimit = 1000)
            )
            offset += 1

            tiltaksgjennomforinger.forEach { it ->
                tiltaksgjennomforingRepository.get(it.id)?.let {
                    tiltaksgjennomforingKafkaProducer.publish(TiltaksgjennomforingDto.from(it))
                }
            }

            count += tiltaksgjennomforinger.size
        } while (tiltaksgjennomforinger.isNotEmpty())

        logger.info("Oppdaterte status for $count tiltaksgjennomføringer")
    }
}
