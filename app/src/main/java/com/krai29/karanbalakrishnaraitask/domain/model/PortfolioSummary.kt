package com.krai29.karanbalakrishnaraitask.domain.model

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnl: Double,
    val todaysPnl: Double,
    val totalPnlPercent: Double
)