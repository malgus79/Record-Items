package com.recorditemsapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.recorditemsapp.model.entity.RecordEntity

@Dao
interface RecordDao {
    @Query("SELECT * FROM RecordEntity")
    fun getAllRecords(): LiveData<MutableList<RecordEntity>>

    @Query("SELECT * FROM RecordEntity where id = :id")
    fun getRecordById(id: Long): LiveData<RecordEntity>

    @Query("SELECT * FROM RecordEntity where name = :name")
    fun getRecordByName(name: String): LiveData<RecordEntity>

    @Insert
    suspend fun addRecord(recordEntity: RecordEntity): Long

    @Update
    suspend fun updateRecord(recordEntity: RecordEntity): Int

    @Delete
    suspend fun deleteRecord(recordEntity: RecordEntity): Int
}