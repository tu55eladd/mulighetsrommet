package no.nav.mulighetsrommet.arena.adapter.consumers

import arrow.core.continuations.either
import arrow.core.leftIfNull
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import no.nav.mulighetsrommet.arena.adapter.ConsumerConfig
import no.nav.mulighetsrommet.arena.adapter.MulighetsrommetApiClient
import no.nav.mulighetsrommet.arena.adapter.clients.ArenaOrdsProxyClient
import no.nav.mulighetsrommet.arena.adapter.models.ArenaEventData
import no.nav.mulighetsrommet.arena.adapter.models.ConsumptionError
import no.nav.mulighetsrommet.arena.adapter.models.arena.ArenaTables
import no.nav.mulighetsrommet.arena.adapter.models.arena.ArenaTiltaksgjennomforing
import no.nav.mulighetsrommet.arena.adapter.models.arena.JaNeiStatus
import no.nav.mulighetsrommet.arena.adapter.models.db.ArenaEvent
import no.nav.mulighetsrommet.arena.adapter.models.db.Sak
import no.nav.mulighetsrommet.arena.adapter.models.db.Tiltaksgjennomforing
import no.nav.mulighetsrommet.arena.adapter.repositories.ArenaEventRepository
import no.nav.mulighetsrommet.arena.adapter.services.ArenaEntityService
import no.nav.mulighetsrommet.arena.adapter.utils.AktivitetsplanenLaunchDate
import no.nav.mulighetsrommet.arena.adapter.utils.ArenaUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import no.nav.mulighetsrommet.domain.dbo.TiltaksgjennomforingDbo as MrTiltaksgjennomforing

class TiltakgjennomforingEndretConsumer(
    override val config: ConsumerConfig,
    override val events: ArenaEventRepository,
    private val entities: ArenaEntityService,
    private val client: MulighetsrommetApiClient,
    private val ords: ArenaOrdsProxyClient
) : ArenaTopicConsumer(
    ArenaTables.Tiltaksgjennomforing
) {

    override val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun decodeArenaData(payload: JsonElement): ArenaEvent {
        val decoded = ArenaEventData.decode<ArenaTiltaksgjennomforing>(payload)

        return ArenaEvent(
            arenaTable = decoded.table,
            arenaId = decoded.data.TILTAKGJENNOMFORING_ID.toString(),
            payload = payload,
            status = ArenaEvent.ConsumptionStatus.Pending
        )
    }

    override suspend fun handleEvent(event: ArenaEvent) = either<ConsumptionError, ArenaEvent.ConsumptionStatus> {
        val decoded = ArenaEventData.decode<ArenaTiltaksgjennomforing>(event.payload)

        ensure(isRegisteredAfterAktivitetsplanen(decoded.data)) {
            ConsumptionError.Ignored("Tiltaksgjennomføring ignorert fordi den ble opprettet før Aktivitetsplanen")
        }

        val mapping = entities.getOrCreateMapping(event)
        val tiltaksgjennomforing = decoded.data
            .toTiltaksgjennomforing(mapping.entityId)
            .let { entities.upsertTiltaksgjennomforing(it) }
            .bind()

        val tiltakstypeMapping = entities
            .getMapping(ArenaTables.Tiltakstype, tiltaksgjennomforing.tiltakskode)
            .bind()
        val sak = entities
            .getSak(tiltaksgjennomforing.sakId)
            .bind()
        val virksomhetsnummer = tiltaksgjennomforing.arrangorId?.let { id ->
            ords.getArbeidsgiver(id)
                .mapLeft { ConsumptionError.fromResponseException(it) }
                .leftIfNull { ConsumptionError.InvalidPayload("Fant ikke arrangør i Arena ORDS for arrangorId=${tiltaksgjennomforing.arrangorId}") }
                .map { it.virksomhetsnummer }
                .bind()
        }
        val mrTiltaksgjennomforing = tiltaksgjennomforing.toDomain(tiltakstypeMapping.entityId, sak, virksomhetsnummer)

        val method = if (decoded.operation == ArenaEventData.Operation.Delete) HttpMethod.Delete else HttpMethod.Put
        client.request(method, "/api/v1/internal/arena/tiltaksgjennomforing", mrTiltaksgjennomforing)
            .mapLeft { ConsumptionError.fromResponseException(it) }
            .map { ArenaEvent.ConsumptionStatus.Processed }
            .bind()
    }

    private fun isRegisteredAfterAktivitetsplanen(data: ArenaTiltaksgjennomforing): Boolean {
        return !ArenaUtils.parseTimestamp(data.REG_DATO).isBefore(AktivitetsplanenLaunchDate)
    }

    private fun ArenaTiltaksgjennomforing.toTiltaksgjennomforing(id: UUID) = Tiltaksgjennomforing(
        id = id,
        tiltaksgjennomforingId = TILTAKGJENNOMFORING_ID,
        sakId = SAK_ID,
        tiltakskode = TILTAKSKODE,
        arrangorId = ARBGIV_ID_ARRANGOR,
        navn = LOKALTNAVN,
        fraDato = ArenaUtils.parseNullableTimestamp(DATO_FRA),
        tilDato = ArenaUtils.parseNullableTimestamp(DATO_TIL),
        apentForInnsok = STATUS_TREVERDIKODE_INNSOKNING != JaNeiStatus.Nei,
        antallPlasser = ANTALL_DELTAKERE,
        status = TILTAKSTATUSKODE
    )

    private fun Tiltaksgjennomforing.toDomain(tiltakstypeId: UUID, sak: Sak, virksomhetsnummer: String?) =
        MrTiltaksgjennomforing(
            id = id,
            navn = navn,
            tiltakstypeId = tiltakstypeId,
            tiltaksnummer = "${sak.aar}#${sak.lopenummer}",
            virksomhetsnummer = virksomhetsnummer,
            startDato = fraDato?.toLocalDate(),
            sluttDato = tilDato?.toLocalDate(),
            enhet = sak.enhet
        )
}
