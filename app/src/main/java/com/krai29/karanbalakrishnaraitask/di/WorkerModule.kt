package com.krai29.karanbalakrishnaraitask.di

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.krai29.karanbalakrishnaraitask.worker.PortfolioSyncWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    private const val PORTFOLIO_SYNC_WORK = "portfolio_sync_work"

    @Provides
    @Singleton
    fun schedulePortfolioSync(@ApplicationContext context: Context): WorkManager {
        val workManager = WorkManager.getInstance(context)
        val request = PeriodicWorkRequestBuilder<PortfolioSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            PORTFOLIO_SYNC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        return workManager
    }
}
