package com.krai29.karanbalakrishnaraitask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = true
)
abstract class HoldingsDatabase : RoomDatabase(){

    abstract fun holdingDao(): HoldingDao
}