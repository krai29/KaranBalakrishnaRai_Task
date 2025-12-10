package com.krai29.karanbalakrishnaraitask.viewmodel

import androidx.paging.PagingData
import com.krai29.karanbalakrishnaraitask.MainDispatcherRule
import com.krai29.karanbalakrishnaraitask.core.DomainResult
import com.krai29.karanbalakrishnaraitask.domain.model.Holding
import com.krai29.karanbalakrishnaraitask.domain.model.PortfolioSummary
import com.krai29.karanbalakrishnaraitask.domain.usecase.ObserveHoldingsUseCase
import com.krai29.karanbalakrishnaraitask.domain.usecase.ObserveSummaryUseCase
import com.krai29.karanbalakrishnaraitask.domain.usecase.RefreshHoldingsUseCase
import com.krai29.karanbalakrishnaraitask.presentation.model.HoldingUiModel
import com.krai29.karanbalakrishnaraitask.presentation.viewmodel.PortfolioViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var observeHoldingsUseCase: ObserveHoldingsUseCase

    @MockK
    lateinit var observeSummaryUseCase: ObserveSummaryUseCase

    @MockK
    lateinit var refreshHoldingsUseCase: RefreshHoldingsUseCase

    private val dummySummary = PortfolioSummary(
        currentValue = 1000.0,
        totalInvestment = 800.0,
        totalPnl = 200.0,
        todaysPnl = 10.0,
        totalPnlPercent = 25.0
    )

    private fun createViewModel(): PortfolioViewModel {
        return PortfolioViewModel(
            observeHoldingsUseCase = observeHoldingsUseCase,
            observeSummaryUseCase = observeSummaryUseCase,
            refreshHoldingsUseCase = refreshHoldingsUseCase
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { observeHoldingsUseCase.invoke(any()) } returns
                flowOf(PagingData.from(emptyList<Holding>()))

        every { observeSummaryUseCase.invoke() } returns flowOf(dummySummary)

        coEvery { refreshHoldingsUseCase.invoke() } returns DomainResult.Success(Unit)
    }

    @Test
    fun `init loads summary and completes refresh on success`() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(dummySummary, state.summary)
        assertEquals(null, state.errorMessage)
        assertFalse(state.isSummaryExpanded)
    }

    @Test
    fun `refresh exposes error when use case fails`() = runTest {
        val error = RuntimeException("network failure")
        coEvery { refreshHoldingsUseCase.invoke() } returns DomainResult.Error(error)

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("network failure", state.errorMessage)
    }

    @Test
    fun `refresh keeps loading state when use case returns Loading`() = runTest {
        coEvery { refreshHoldingsUseCase.invoke() } returns DomainResult.Loading

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `toggleSummaryExpanded flips flag`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val initial = viewModel.uiState.value.isSummaryExpanded
        viewModel.toggleSummaryExpanded()
        val afterFirst = viewModel.uiState.value.isSummaryExpanded
        viewModel.toggleSummaryExpanded()
        val afterSecond = viewModel.uiState.value.isSummaryExpanded

        assertTrue(afterFirst != initial)
        assertEquals(initial, afterSecond)
    }

    @Test
    fun `holdingsPaging uses HOLDINGS_PAGE_SIZE and maps to ui models`() = runTest {
        val domainHolding = Holding(
            symbol = "ABC",
            quantity = 3,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )

        val pageSizeSlot = slot<Int>()

        every { observeHoldingsUseCase.invoke(capture(pageSizeSlot)) } returns
                flowOf(PagingData.from(listOf(domainHolding)))

        every { observeSummaryUseCase.invoke() } returns flowOf(dummySummary)
        coEvery { refreshHoldingsUseCase.invoke() } returns DomainResult.Success(Unit)

        val viewModel = createViewModel()

        val paging: PagingData<HoldingUiModel> = viewModel.holdingsPaging.first()
        assertNotNull(paging)

        assertEquals(
            PortfolioViewModel.HOLDINGS_PAGE_SIZE,
            pageSizeSlot.captured
        )
    }

    @Test
    fun `uiState updates when summary flow emits new value`() = runTest {
        val summaryFlow = MutableStateFlow(dummySummary)
        every { observeSummaryUseCase.invoke() } returns summaryFlow

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(dummySummary, viewModel.uiState.value.summary)

        val updated = dummySummary.copy(currentValue = 2000.0, totalPnl = 1200.0)
        summaryFlow.value = updated
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(updated, state.summary)
        assertFalse(state.isLoading)
        assertEquals(null, state.errorMessage)
    }
}
