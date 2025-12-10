package com.krai29.karanbalakrishnaraitask.domain.usecase

import androidx.paging.PagingData
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHoldingsUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    operator fun invoke(pageSize: Int = 10): Flow<PagingData<Holding>> {
        return repository.observeHoldingsPaged(pageSize)
    }
}