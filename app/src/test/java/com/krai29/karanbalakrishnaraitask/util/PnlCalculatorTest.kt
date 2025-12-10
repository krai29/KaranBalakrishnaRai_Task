package com.krai29.karanbalakrishnaraitask.util

import com.krai29.karanbalakrishnaraitask.util.PnlCalculator.roundToMoney
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import org.junit.Assert.assertEquals
import org.junit.Test

class PnlCalculatorTest {

    @Test
    fun `summary with single holding computes correct values`() {
        val holdings = listOf(
            Holding(
                symbol = "ABC",
                quantity = 10,
                ltp = 120.0,
                avgPrice = 100.0,
                close = 115.0
            )
        )

        val summary = PnlCalculator.summary(holdings)

        val expectedCurrentValue = 120.0 * 10
        val expectedTotalInvestment = 100.0 * 10
        val expectedTotalPnl = expectedCurrentValue - expectedTotalInvestment
        val expectedTodaysPnl = (115.0 - 120.0) * 10
        val expectedPercent = (expectedTotalPnl / expectedTotalInvestment) * 100.0

        assertEquals(expectedCurrentValue, summary.currentValue, 0.0001)
        assertEquals(expectedTotalInvestment, summary.totalInvestment, 0.0001)
        assertEquals(expectedTotalPnl, summary.totalPnl, 0.0001)
        assertEquals(expectedTodaysPnl, summary.todaysPnl, 0.0001)
        assertEquals(expectedPercent, summary.totalPnlPercent, 0.0001)
    }

    @Test
    fun `summary with zero total investment sets percent to zero`() {
        val holdings = listOf(
            Holding(
                symbol = "NO_INVEST",
                quantity = 5,
                ltp = 50.0,
                avgPrice = 0.0,
                close = 48.0
            )
        )

        val summary = PnlCalculator.summary(holdings)

        assertEquals(0.0, summary.totalInvestment, 0.0001)
        assertEquals(0.0, summary.totalPnlPercent, 0.0001)
    }

    @Test
    fun `summary with multiple holdings accumulates values`() {
        val holdings = listOf(
            Holding(
                symbol = "AAA",
                quantity = 2,
                ltp = 100.0,
                avgPrice = 80.0,
                close = 98.0
            ),
            Holding(
                symbol = "BBB",
                quantity = 3,
                ltp = 200.0,
                avgPrice = 150.0,
                close = 210.0
            )
        )

        val summary = PnlCalculator.summary(holdings)

        val expectedCurrentValue =
            (2 * 100.0) + (3 * 200.0)
        val expectedTotalInvestment =
            (2 * 80.0) + (3 * 150.0)
        val expectedTotalPnl = expectedCurrentValue - expectedTotalInvestment
        val expectedTodaysPnl =
            (98.0 - 100.0) * 2 + (210.0 - 200.0) * 3
        val expectedPercent = (expectedTotalPnl / expectedTotalInvestment) * 100.0

        assertEquals(expectedCurrentValue, summary.currentValue, 0.0001)
        assertEquals(expectedTotalInvestment, summary.totalInvestment, 0.0001)
        assertEquals(expectedTotalPnl, summary.totalPnl, 0.0001)
        assertEquals(expectedTodaysPnl, summary.todaysPnl, 0.0001)
        assertEquals(expectedPercent, summary.totalPnlPercent, 0.0001)
    }

    @Test
    fun `holdingPnl returns correct positive value`() {
        val holding = Holding(
            symbol = "POS",
            quantity = 4,
            ltp = 150.0,
            avgPrice = 100.0,
            close = 145.0
        )

        val pnl = PnlCalculator.holdingPnl(holding)

        assertEquals(200.0, pnl, 0.0001)
    }

    @Test
    fun `holdingPnl returns correct negative value`() {
        val holding = Holding(
            symbol = "NEG",
            quantity = 2,
            ltp = 80.0,
            avgPrice = 100.0,
            close = 90.0
        )

        val pnl = PnlCalculator.holdingPnl(holding)

        assertEquals(-40.0, pnl, 0.0001)
    }

    @Test
    fun `roundToMoney rounds positive value to 2 decimals`() {
        val value = 12.349
        val rounded = value.roundToMoney()

        assertEquals(12.35, rounded, 0.0001)
    }

    @Test
    fun `roundToMoney rounds positive value down correctly`() {
        val value = 12.341
        val rounded = value.roundToMoney()

        assertEquals(12.34, rounded, 0.0001)
    }

    @Test
    fun `roundToMoney rounds negative value to 2 decimals`() {
        val value = -7.671
        val rounded = value.roundToMoney()

        assertEquals(-7.67, rounded, 0.0001)
    }
}
