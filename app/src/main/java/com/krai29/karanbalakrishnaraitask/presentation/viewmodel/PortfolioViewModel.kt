package com.krai29.karanbalakrishnaraitask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.krai29.karanbalakrishnaraitask.domain.usecase.ObserveHoldingsUseCase
import com.krai29.karanbalakrishnaraitask.domain.usecase.ObserveSummaryUseCase
import com.krai29.karanbalakrishnaraitask.domain.usecase.RefreshHoldingsUseCase
import com.krai29.karanbalakrishnaraitask.presentation.model.HoldingUiModel
import com.krai29.karanbalakrishnaraitask.presentation.model.PortfolioUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.krai29.karanbalakrishnaraitask.core.DomainResult

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    observeHoldingsUseCase: ObserveHoldingsUseCase,
    private val observeSummaryUseCase: ObserveSummaryUseCase,
    private val refreshHoldingsUseCase: RefreshHoldingsUseCase
) : ViewModel() {

    companion object {
        const val HOLDINGS_PAGE_SIZE = 10
    }

    private val _uiState = MutableStateFlow(PortfolioUiState(isLoading = true))
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    val holdingsPaging: Flow<PagingData<HoldingUiModel>> =
        observeHoldingsUseCase(pageSize = HOLDINGS_PAGE_SIZE)
            .map { pagingData ->
                pagingData.map { HoldingUiModel.fromDomain(it) }
            }
            .cachedIn(viewModelScope)

    init {
        observeSummary()
        refresh()
    }

    private fun observeSummary() {
        viewModelScope.launch {
            observeSummaryUseCase().collect { summary ->
                _uiState.update {
                    it.copy(
                        summary = summary,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = refreshHoldingsUseCase()) {
                is DomainResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                is DomainResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.throwable.message ?: "Something went wrong"
                        )
                    }
                }
                DomainResult.Loading -> Unit
            }
        }
    }

    fun toggleSummaryExpanded() {
        _uiState.update { it.copy(isSummaryExpanded = !it.isSummaryExpanded) }
    }
}
