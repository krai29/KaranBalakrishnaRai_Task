package com.krai29.karanbalakrishnaraitask.data.repository

import com.krai29.karanbalakrishnaraitask.core.DomainResult
import com.krai29.karanbalakrishnaraitask.core.NetworkMonitor
import com.krai29.karanbalakrishnaraitask.data.local.HoldingDao
import com.krai29.karanbalakrishnaraitask.data.local.HoldingEntity
import com.krai29.karanbalakrishnaraitask.data.remote.PortfolioApi
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.domain.repository.HoldingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class HoldingsRepositoryImpl @Inject constructor(
    private val api: PortfolioApi,
    private val dao: HoldingDao,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor
) : HoldingsRepository {

    override fun observeHoldingsPaged(pageSize: Int): Flow<PagingData<Holding>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.holdingsPagingSource() }
        ).flow
            .map { paging ->
                paging.map { it.toDomain() }
            }
            .flowOn(ioDispatcher)
    }

    override fun observeAllHoldings(): Flow<List<Holding>> {
        return dao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun refreshRemote(): DomainResult<Unit> =
        withContext(ioDispatcher) {
            try {
                if (!networkMonitor.isOnline()) {
                    val hasLocal = dao.count() > 0
                    val msg = if (hasLocal) {
                        "No internet connection"
                    } else {
                        "No internet connection and no cached data"
                    }
                    return@withContext DomainResult.Error(IOException(msg))
                }

                val response = api.getHoldings()
                val entities = response.data.userHolding.map { dto ->
                    HoldingEntity(
                        symbol = dto.symbol,
                        quantity = dto.quantity,
                        ltp = dto.ltp,
                        avgPrice = dto.avgPrice,
                        close = dto.close,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                }
                dao.clearAll()
                dao.upsertAll(entities)
                DomainResult.Success(Unit)
            } catch (t: Throwable) {
                DomainResult.Error(t)
            }
        }

    private fun HoldingEntity.toDomain(): Holding = Holding(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice,
        close = close
    )
}
