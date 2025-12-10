package com.krai29.karanbalakrishnaraitask.util

import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.domain.model.PortfolioSummary
import kotlin.math.abs
import kotlin.math.round

object PnlCalculator {

    fun summary(holdings: List<Holding>): PortfolioSummary {
        var currentValue = 0.0
        var totalInvestment = 0.0
        var todaysPnl = 0.0

        for (h in holdings) {
            val cv = h.ltp * h.quantity
            currentValue += cv
            val inv = h.avgPrice * h.quantity
            totalInvestment += inv
            todaysPnl += (h.close - h.ltp) * h.quantity
        }

        val totalPnl = currentValue - totalInvestment
        val pnlPercent = if (abs(totalInvestment) < 0.0001) {
            0.0
        } else {
            (totalPnl / totalInvestment) * 100.0
        }

        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnl = totalPnl,
            todaysPnl = todaysPnl,
            totalPnlPercent = pnlPercent
        )
    }

    fun holdingPnl(holding: Holding): Double {
        return (holding.ltp - holding.avgPrice) * holding.quantity
    }

    fun Double.roundToMoney(): Double {
        return round(this * 100.0) / 100.0
    }
}