package com.krai29.karanbalakrishnaraitask.presentation.model

import com.krai29.karanbalakrishnaraitask.domain.model.PortfolioSummary

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val summary: PortfolioSummary? = null,
    val isSummaryExpanded: Boolean = false
)