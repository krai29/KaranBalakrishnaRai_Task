package com.krai29.karanbalakrishnaraitask.domain.usecase

import com.krai29.karanbalakrishnaraitask.domain.repository.HoldingsRepository
import com.krai29.karanbalakrishnaraitask.core.DomainResult
import javax.inject.Inject

class RefreshHoldingsUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    suspend operator fun invoke(): DomainResult<Unit> = repository.refreshRemote()
}