package com.krai29.karanbalakrishnaraitask.domain.usecase

import com.krai29.karanbalakrishnaraitask.domain.model.PortfolioSummary
import com.krai29.karanbalakrishnaraitask.domain.repository.HoldingsRepository
import com.krai29.karanbalakrishnaraitask.util.PnlCalculator
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSummaryUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return repository.observeAllHoldings().map { holdings ->
            PnlCalculator.summary(holdings)
        }
    }
}