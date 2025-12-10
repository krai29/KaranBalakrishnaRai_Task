package com.krai29.karanbalakrishnaraitask.presentation.model

import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.util.PnlCalculator.holdingPnl
import com.krai29.karanbalakrishnaraitask.util.PnlCalculator.roundToMoney

data class HoldingUiModel(
    val symbol: String,
    val quantity: Int,
    val ltp: String,
    val pnl: String,
    val pnlPositive: Boolean
) {
    companion object {
        fun fromDomain(h: Holding): HoldingUiModel {
            val pnlRaw = holdingPnl(h).roundToMoney()
            val pnlPositive = pnlRaw >= 0
            val pnlFormatted = formatCurrency(pnlRaw)
            val ltpFormatted = formatCurrency(h.ltp)
            return HoldingUiModel(
                symbol = h.symbol,
                quantity = h.quantity,
                ltp = ltpFormatted,
                pnl = pnlFormatted,
                pnlPositive = pnlPositive
            )
        }

        fun formatCurrency(value: Double): String {
            return "₹%.2f".format(value)
        }
    }
}