package no.nav.mulighetsrommet.api.tasks

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay
import kotlinx.coroutines.runBlocking
import no.nav.mulighetsrommet.api.services.Norg2Service
import no.nav.mulighetsrommet.slack_notifier.SlackNotifier
import org.slf4j.LoggerFactory

class SynchronizeNorgEnheter(config: Config, norg2Service: Norg2Service, slackNotifier: SlackNotifier) {
    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config(
        val delayOfMinutes: Int,
        val schedulerStatePollDelay: Long = 1000
    )

    val task: RecurringTask<Void> = Tasks
        .recurring("synchronize-norg2-enheter", FixedDelay.ofMinutes(config.delayOfMinutes))
        .onFailure { _, _ ->
            slackNotifier.sendMessage("Klarte ikke synkronisere enheter fra NORG2. Konsekvensen er at ingen enheter blir oppdaterte i databasen og vi kan potensielt vise feil enheter til bruker i admin-flate.")
        }
        .execute { _, _ ->
            runBlocking {
                logger.info("Kjører synkronisering av NORG2-enheter")
                norg2Service.synkroniserEnheter()
            }
        }
}
