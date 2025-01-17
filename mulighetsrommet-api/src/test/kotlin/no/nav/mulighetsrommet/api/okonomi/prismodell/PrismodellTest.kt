package no.nav.mulighetsrommet.api.okonomi.prismodell

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PrismodellTest : FunSpec({
    context("AFT tilsagn beregning") {
        test("en plass en måned = sats") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 1,
                periodeStart = LocalDate.of(2024, 1, 1),
                periodeSlutt = LocalDate.of(2024, 1, 31),
            ) shouldBe 20205
        }
        test("flere plasser en måned") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 6,
                periodeStart = LocalDate.of(2024, 1, 1),
                periodeSlutt = LocalDate.of(2024, 1, 31),
            ) shouldBe 20205 * 6
        }
        test("en plass halv måned") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 19500,
                antallPlasser = 1,
                periodeStart = LocalDate.of(2023, 4, 1),
                periodeSlutt = LocalDate.of(2023, 4, 15),
            ) shouldBe 19500 / 2
        }
        test("flere plasser en og en halv måned") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 10,
                periodeStart = LocalDate.of(2024, 3, 1),
                periodeSlutt = LocalDate.of(2024, 4, 15),
            ) shouldBe 303075
        }
        test("ingen plasser") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 0,
                periodeStart = LocalDate.of(2024, 3, 1),
                periodeSlutt = LocalDate.of(2024, 4, 15),
            ) shouldBe 0
        }
        test("skuddår/ikke skuddår") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 19500,
                antallPlasser = 1,
                periodeStart = LocalDate.of(2023, 2, 1),
                periodeSlutt = LocalDate.of(2023, 2, 28),
            ) shouldBe 19500

            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 1,
                periodeStart = LocalDate.of(2024, 2, 1),
                periodeSlutt = LocalDate.of(2024, 2, 28),
            ) shouldBe 19599
        }
        test("feil sats kaster exception") {
            shouldThrow<IllegalArgumentException> {
                Prismodell.AFT.beregnTilsagnBelop(
                    sats = 15,
                    antallPlasser = 1,
                    periodeStart = LocalDate.of(2023, 2, 1),
                    periodeSlutt = LocalDate.of(2023, 2, 28),
                )
            }
        }
        test("tom periode kaster exception") {
            shouldThrow<IllegalArgumentException> {
                Prismodell.AFT.beregnTilsagnBelop(
                    sats = 19500,
                    antallPlasser = 7,
                    periodeStart = LocalDate.of(2023, 2, 2),
                    periodeSlutt = LocalDate.of(2022, 2, 2),
                )
            }
        }
        test("én dag") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 1,
                periodeStart = LocalDate.of(2024, 1, 1),
                periodeSlutt = LocalDate.of(2024, 1, 1),
            ) shouldBe 606
        }
        test("overflow kaster exception") {
            // overflow i en delberegning for én måned
            shouldThrow<ArithmeticException> {
                Prismodell.AFT.beregnTilsagnBelop(
                    sats = 20205,
                    antallPlasser = Int.MAX_VALUE,
                    periodeStart = LocalDate.of(2024, 1, 1),
                    periodeSlutt = LocalDate.of(2024, 1, 31),
                )
            }

            // overflow på summering av 12 måneder
            shouldThrow<ArithmeticException> {
                Prismodell.AFT.beregnTilsagnBelop(
                    sats = 20205,
                    antallPlasser = 9500,
                    periodeStart = LocalDate.of(2024, 1, 1),
                    periodeSlutt = LocalDate.of(2024, 12, 31),
                )
            }
        }
        test("reelt eksempel nr 1") {
            Prismodell.AFT.beregnTilsagnBelop(
                sats = 20205,
                antallPlasser = 24,
                periodeStart = LocalDate.of(2024, 9, 15),
                periodeSlutt = LocalDate.of(2024, 12, 31),
            ) shouldBe 1711768
        }
    }
})
