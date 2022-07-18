package com.recorditemsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recorditemsapp.core.Constants
import com.recorditemsapp.core.RecordException
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.model.entity.RecordEntity
import com.recorditemsapp.model.usecase.MainRecordUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainRecordViewModel : ViewModel() {

    private var useCase: MainRecordUseCase = MainRecordUseCase()
    private val typeError: MutableLiveData<TypeError> = MutableLiveData()
    private val showProgress: MutableLiveData<Boolean> = MutableLiveData()
    private val records = useCase.records

    fun getRecords(): LiveData<MutableList<RecordEntity>> {
        return records
    }

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun isShowProgress(): LiveData<Boolean> {
        return showProgress
    }

    fun deleteRecords(recordEntity: RecordEntity) {
        executeAction { useCase.deleteRecord(recordEntity) }
    }

    fun updateRecord(recordEntity: RecordEntity) {
        recordEntity.isFavorite = !recordEntity.isFavorite
        executeAction { useCase.updateRecord(recordEntity) }
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            showProgress.value = Constants.SHOW

            try {
                block()
            } catch (e: RecordException) {
                typeError.value = e.typeError
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}