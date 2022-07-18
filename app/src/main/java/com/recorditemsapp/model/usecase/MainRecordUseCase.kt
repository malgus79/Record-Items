package com.recorditemsapp.model.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.recorditemsapp.application.RecordApplication
import com.recorditemsapp.core.RecordException
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.model.entity.RecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRecordUseCase {

    val records: LiveData<MutableList<RecordEntity>> = liveData {
        val storesLiveData = RecordApplication.database.recordDao().getAllRecords()
        emitSource(storesLiveData.map { records ->
            records.sortedBy { it.name }.toMutableList()
        })
    }

    suspend fun deleteRecord(recordEntity: RecordEntity) = withContext(Dispatchers.IO) {
        val result = RecordApplication.database.recordDao().deleteRecord(recordEntity)
        if (result == 0) throw RecordException(TypeError.DELETE)
    }

    suspend fun updateRecord(recordEntity: RecordEntity) = withContext(Dispatchers.IO) {
        val result = RecordApplication.database.recordDao().updateRecord(recordEntity)
        if (result == 0) throw RecordException(TypeError.UPDATE)
    }
}