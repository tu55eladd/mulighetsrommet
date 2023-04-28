package no.nav.mulighetsrommet.api.tasks

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay
import kotlinx.coroutines.runBlocking
import no.nav.mulighetsrommet.api.services.SanityService
import no.nav.mulighetsrommet.slack.SlackNotifier
import org.slf4j.LoggerFactory

class SynchronizeTiltaksgjennomforingEnheter(
    config: Config,
    sanityService: SanityService,
    slackNotifier: SlackNotifier,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config(
        val delayOfMinutes: Int,
    )

    val task: RecurringTask<Void> = Tasks
        .recurring("synchronize-tiltaksgjennomforing-enheter", FixedDelay.ofMinutes(config.delayOfMinutes))
        .onFailure { _, _ ->
            logger.error("av tiltaksgjennomforingsstatuser på kafka")
            slackNotifier.sendMessage("Klarte ikke synkronisere tiltaksgjennomføringenheter.")
        }
        .execute { _, _ ->
            runBlocking {
                logger.info("Kjører synkronisering av tiltaksgjennomforingsenheter fra Sanity")
                sanityService.oppdaterTiltaksgjennomforingEnheter()
            }
        }
}
