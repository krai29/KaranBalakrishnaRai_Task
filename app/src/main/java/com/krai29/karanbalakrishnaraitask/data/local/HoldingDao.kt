package com.krai29.karanbalakrishnaraitask.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Query("SELECT * FROM holdings ORDER BY symbol ASC")
    fun holdingsPagingSource(): PagingSource<Int, HoldingEntity>

    @Query("SELECT * FROM holdings ORDER BY symbol ASC")
    fun observeAll(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings")
    suspend fun getAllHoldingsOnce(): List<HoldingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(holdings:List<HoldingEntity>)

    @Query("DELETE FROM holdings")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM holdings")
    suspend fun count(): Int
}