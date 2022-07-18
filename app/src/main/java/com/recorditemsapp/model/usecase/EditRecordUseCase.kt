package com.recorditemsapp.model.usecase

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.recorditemsapp.application.RecordApplication
import com.recorditemsapp.core.RecordException
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.model.entity.RecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditRecordUseCase {

    fun getRecordById(id: Long): LiveData<RecordEntity> {
        return RecordApplication.database.recordDao().getRecordById(id)
    }

    suspend fun saveRecord(recordEntity: RecordEntity) = withContext(Dispatchers.IO) {
        try {
            RecordApplication.database.recordDao().addRecord(recordEntity)
        } catch (e: SQLiteConstraintException) {
            throw RecordException(TypeError.INSERT)
        }
    }

    suspend fun updateRecord(recordEntity: RecordEntity) = withContext(Dispatchers.IO) {
        try {
            val result = RecordApplication.database.recordDao().updateRecord(recordEntity)
            if (result == 0) throw RecordException(TypeError.UPDATE)
        } catch (e: SQLiteConstraintException) {
            throw RecordException(TypeError.UPDATE)
        }
    }
}