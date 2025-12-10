package com.krai29.karanbalakrishnaraitask.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.krai29.karanbalakrishnaraitask.domain.usecase.RefreshHoldingsUseCase
import com.krai29.karanbalakrishnaraitask.core.DomainResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PortfolioSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val refreshHoldingsUseCase: RefreshHoldingsUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            when (refreshHoldingsUseCase()) {
                is DomainResult.Success -> Result.success()
                is DomainResult.Error -> Result.retry()
                DomainResult.Loading -> Result.success()
            }
        }
    }
}
