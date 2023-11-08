package no.nav.mulighetsrommet.unleash.strategies

import io.getunleash.UnleashContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ByNavidentStrategyTest : FunSpec({
    context("Unleash - ByNavidentStrategy") {
        val byNavidentStrategy = ByNavidentStrategy()

        test("Skal returnere false når ingen UnleashContext er sendt inn") {
            byNavidentStrategy.isEnabled(mutableMapOf()) shouldBe false
        }

        test("Skal returnere false når brukers enhet ikke finnes i liste over valgte navIdenter") {
            byNavidentStrategy.isEnabled(
                mutableMapOf(ByNavidentStrategy.VALGT_NAVIDENT_PARAM to "S123456"),
                UnleashContext("T654321", "", "", emptyMap()),
            ) shouldBe false
        }

        test("Skal returnere true når brukers enhet finnes i listen over over valgte navIdenter") {
            byNavidentStrategy.isEnabled(
                mutableMapOf(ByNavidentStrategy.VALGT_NAVIDENT_PARAM to "S123456"),
                UnleashContext("S123456", "", "", emptyMap()),
            ) shouldBe true
        }
    }
})