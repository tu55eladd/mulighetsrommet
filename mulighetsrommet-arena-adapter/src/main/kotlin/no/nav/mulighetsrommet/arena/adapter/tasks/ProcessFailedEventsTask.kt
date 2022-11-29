package no.nav.mulighetsrommet.arena.adapter.tasks

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay
import kotlinx.coroutines.runBlocking
import no.nav.mulighetsrommet.arena.adapter.models.db.ArenaEvent
import no.nav.mulighetsrommet.arena.adapter.services.ArenaEventService

class ProcessFailedEventsTask(private val config: Config, private val arenaEventService: ArenaEventService) {

    data class Config(
        val delayOfMinutes: Int
    )

    fun toTask(): RecurringTask<Void> {
        return Tasks
            .recurring("process-failed-events", FixedDelay.ofMinutes(config.delayOfMinutes))
            .execute { _, _ ->
                runBlocking {
                    arenaEventService.replayEvents(status = ArenaEvent.ConsumptionStatus.Failed)
                }
            }
    }
}
