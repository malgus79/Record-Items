package com.recorditemsapp.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.recorditemsapp.model.dao.RecordDao
import com.recorditemsapp.model.entity.RecordEntity

@Database(entities = [RecordEntity::class], version = 3)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}