package com.krai29.karanbalakrishnaraitask.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krai29.karanbalakrishnaraitask.core.DomainResult
import com.krai29.karanbalakrishnaraitask.core.NetworkMonitor
import com.krai29.karanbalakrishnaraitask.data.local.HoldingDao
import com.krai29.karanbalakrishnaraitask.data.local.HoldingEntity
import com.krai29.karanbalakrishnaraitask.data.remote.HoldingDto
import com.krai29.karanbalakrishnaraitask.data.remote.PortfolioApi
import com.krai29.karanbalakrishnaraitask.data.remote.PortfolioDataDto
import com.krai29.karanbalakrishnaraitask.data.remote.PortfolioResponseDto
import com.krai29.karanbalakrishnaraitask.data.repository.HoldingsRepositoryImpl
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HoldingsRepositoryImplTest {

    private fun createRepository(
        dao: FakeHoldingDao = FakeHoldingDao(),
        api: FakePortfolioApi = FakePortfolioApi(),
        network: FakeNetworkMonitor = FakeNetworkMonitor()
    ): Triple<HoldingsRepositoryImpl, FakeHoldingDao, FakePortfolioApi> {

        val repo = HoldingsRepositoryImpl(
            api = api,
            dao = dao,
            ioDispatcher = Dispatchers.Unconfined,
            networkMonitor = network
        )
        return Triple(repo, dao, api)
    }


    @Test
    fun `refreshRemote stores remote data when online`() = runTest {
        val dao = FakeHoldingDao()
        val api = FakePortfolioApi()
        val network = FakeNetworkMonitor(isOnline = true)

        val (repo, _, _) = createRepository(dao, api, network)

        api.response = PortfolioResponseDto(
            PortfolioDataDto(
                userHolding = listOf(
                    HoldingDto("ABC", 1, 100.0, 80.0, 95.0),
                    HoldingDto("XYZ", 2, 200.0, 150.0, 190.0)
                )
            )
        )

        val result = repo.refreshRemote()
        assertTrue(result is DomainResult.Success)

        val stored = dao.getAllHoldingsOnce()
        assertEquals(2, stored.size)
        assertEquals("ABC", stored[0].symbol)
        assertEquals("XYZ", stored[1].symbol)
    }

    @Test
    fun `refreshRemote returns error when offline with cache`() = runTest {
        val dao = FakeHoldingDao()
        dao.upsertAll(
            listOf(
                HoldingEntity("ABC", 1, 100.0, 80.0, 95.0, 0L)
            )
        )
        val network = FakeNetworkMonitor(isOnline = false)
        val api = FakePortfolioApi()

        val (repo, _, _) = createRepository(dao, api, network)
        val result = repo.refreshRemote()

        assertTrue(result is DomainResult.Error)
        val message = (result as DomainResult.Error).throwable.message ?: ""
        assertTrue(message.contains("No internet connection"))
        assertEquals(1, dao.count())
    }

    @Test
    fun `refreshRemote returns error when offline without cache`() = runTest {
        val dao = FakeHoldingDao()
        val network = FakeNetworkMonitor(isOnline = false)
        val api = FakePortfolioApi()

        val (repo, _, _) = createRepository(dao, api, network)
        val result = repo.refreshRemote()

        assertTrue(result is DomainResult.Error)
        val message = (result as DomainResult.Error).throwable.message ?: ""
        assertTrue(message.contains("No internet connection and no cached data"))
    }

    @Test
    fun `refreshRemote wraps exception from api`() = runTest {
        val dao = FakeHoldingDao()
        val network = FakeNetworkMonitor(isOnline = true)
        val api = FakePortfolioApi().apply {
            throwable = IOException("server boom")
        }

        val (repo, _, _) = createRepository(dao, api, network)
        val result = repo.refreshRemote()

        assertTrue(result is DomainResult.Error)
        val message = (result as DomainResult.Error).throwable.message
        assertEquals("server boom", message)
    }

    @Test
    fun `observeAllHoldings maps entities to domain model`() = runTest {
        val dao = FakeHoldingDao().apply {
            upsertAll(
                listOf(
                    HoldingEntity("ABC", 1, 100.0, 80.0, 95.0, 0L),
                    HoldingEntity("XYZ", 2, 200.0, 150.0, 190.0, 0L)
                )
            )
        }
        val (repo, _, _) = createRepository(dao = dao)

        val list: List<Holding> = repo.observeAllHoldings().first()

        assertEquals(2, list.size)
        assertEquals("ABC", list[0].symbol)
        assertEquals(1, list[0].quantity)
        assertEquals(100.0, list[0].ltp, 0.0)
    }

    @Test
    fun `observeHoldingsPaged creates paging flow`() = runTest {
        val dao = FakeHoldingDao().apply {
            upsertAll(
                listOf(
                    HoldingEntity("ABC", 1, 100.0, 80.0, 95.0, 0L)
                )
            )
        }
        val (repo, _, _) = createRepository(dao = dao)

        val flow = repo.observeHoldingsPaged(pageSize = 5)
        val pagingData = flow.first()

        assertNotNull(pagingData)
    }


    private class FakeHoldingDao : HoldingDao {

        private val backing = MutableStateFlow<List<HoldingEntity>>(emptyList())

        override fun holdingsPagingSource(): PagingSource<Int, HoldingEntity> {
            return object : PagingSource<Int, HoldingEntity>() {
                override fun getRefreshKey(state: PagingState<Int, HoldingEntity>): Int? = null

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HoldingEntity> {
                    val data = backing.value.sortedBy { it.symbol }
                    return LoadResult.Page(
                        data = data,
                        prevKey = null,
                        nextKey = null
                    )
                }
            }
        }

        override fun observeAll(): Flow<List<HoldingEntity>> = backing

        override suspend fun getAllHoldingsOnce(): List<HoldingEntity> = backing.value

        override suspend fun upsertAll(holdings: List<HoldingEntity>) {
            backing.value = holdings
        }

        override suspend fun clearAll() {
            backing.value = emptyList()
        }

        override suspend fun count(): Int = backing.value.size
    }

    private class FakePortfolioApi : PortfolioApi {
        var response: PortfolioResponseDto? = null
        var throwable: Throwable? = null

        override suspend fun getHoldings(
            page: Int?,
            pageSize: Int?
        ): PortfolioResponseDto {
            throwable?.let { throw it }
            return response ?: run {
                fail("FakePortfolioApi.response was not set")
                PortfolioResponseDto(PortfolioDataDto(emptyList()))
            }
        }
    }

    private class FakeNetworkMonitor(
        private val isOnline: Boolean = true
    ) : NetworkMonitor {
        override fun isOnline(): Boolean = isOnline
    }

}
