package com.krai29.karanbalakrishnaraitask.di

import com.krai29.karanbalakrishnaraitask.data.repository.HoldingsRepositoryImpl
import com.krai29.karanbalakrishnaraitask.domain.repository.HoldingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHoldingsRepository(
        impl: HoldingsRepositoryImpl
    ): HoldingsRepository
}
