package com.krai29.karanbalakrishnaraitask.domain.repository

import androidx.paging.PagingData
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.core.DomainResult
import kotlinx.coroutines.flow.Flow

interface HoldingsRepository {

    fun observeHoldingsPaged(pageSize: Int): Flow<PagingData<Holding>>

    fun observeAllHoldings(): Flow<List<Holding>>

    suspend fun refreshRemote(): DomainResult<Unit>
}