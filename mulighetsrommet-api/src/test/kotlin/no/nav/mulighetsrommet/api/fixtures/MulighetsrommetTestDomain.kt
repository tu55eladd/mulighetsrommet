package no.nav.mulighetsrommet.api.fixtures

import io.kotest.assertions.arrow.core.shouldBeRight
import no.nav.mulighetsrommet.api.clients.norg2.Norg2Type
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetDbo
import no.nav.mulighetsrommet.api.domain.dbo.NavEnhetStatus
import no.nav.mulighetsrommet.api.fixtures.NavAnsattFixture.ansatt1
import no.nav.mulighetsrommet.api.fixtures.NavAnsattFixture.ansatt2
import no.nav.mulighetsrommet.api.repositories.*
import no.nav.mulighetsrommet.database.FlywayDatabaseAdapter

data class MulighetsrommetTestDomain(
    val enhet: NavEnhetDbo = NavEnhetDbo(
        navn = "IT",
        enhetsnummer = "2990",
        status = NavEnhetStatus.AKTIV,
        type = Norg2Type.DIR,
        overordnetEnhet = null,
    ),

) {
    fun initialize(database: FlywayDatabaseAdapter) {
        val enheter = NavEnhetRepository(database)
        enheter.upsert(enhet).shouldBeRight()

        val ansatte = NavAnsattRepository(database)
        ansatte.upsert(ansatt1).shouldBeRight()
        ansatte.upsert(ansatt2).shouldBeRight()

        val tiltakstyper = TiltakstypeRepository(database)
        tiltakstyper.upsert(TiltakstypeFixtures.Oppfolging).shouldBeRight()
        tiltakstyper.upsert(TiltakstypeFixtures.Arbeidstrening).shouldBeRight()

        val avtaler = AvtaleRepository(database)
        avtaler.upsert(AvtaleFixtures.avtale1)

        val tiltaksgjennomforinger = TiltaksgjennomforingRepository(database)
        tiltaksgjennomforinger.upsert(TiltaksgjennomforingFixtures.Oppfolging1).shouldBeRight()
        tiltaksgjennomforinger.upsert(TiltaksgjennomforingFixtures.Oppfolging2).shouldBeRight()
        tiltaksgjennomforinger.upsert(TiltaksgjennomforingFixtures.Arbeidstrening1).shouldBeRight()
    }
}
