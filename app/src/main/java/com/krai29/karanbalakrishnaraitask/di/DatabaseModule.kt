package com.krai29.karanbalakrishnaraitask.di

import android.content.Context
import androidx.room.Room
import com.krai29.karanbalakrishnaraitask.data.local.HoldingDao
import com.krai29.karanbalakrishnaraitask.data.local.HoldingsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HoldingsDatabase{
        return Room.databaseBuilder(
                context,
                HoldingsDatabase::class.java,
                "holding.db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideHoldingDao(db: HoldingsDatabase): HoldingDao = db.holdingDao()

}