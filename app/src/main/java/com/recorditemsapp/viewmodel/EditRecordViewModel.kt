package com.recorditemsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recorditemsapp.core.RecordException
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.model.entity.RecordEntity
import com.recorditemsapp.model.usecase.EditRecordUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditRecordViewModel : ViewModel() {

    private var recordId: Long = 0
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()
    private val useCase: EditRecordUseCase = EditRecordUseCase()
    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun setTypeError(typeError: TypeError) {
        this.typeError.value = typeError
    }

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun setRecordSelected(recordEntity: RecordEntity) {
        recordId = recordEntity.id
    }

    fun getRecordSelected(): LiveData<RecordEntity> {
        return useCase.getRecordById(recordId)
    }

    fun setShowFab(isVisible: Boolean) {
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean> {
        return showFab
    }

    fun setResult(value: Any) {
        result.value = value
    }

    fun getResult(): LiveData<Any> {
        return result
    }

    fun saveRecord(recordEntity: RecordEntity) {
        executeAction(recordEntity) { useCase.saveRecord(recordEntity) }
    }

    fun updateRecord(recordEntity: RecordEntity) {
        executeAction(recordEntity) { useCase.updateRecord(recordEntity) }
    }

    private fun executeAction(recordEntity: RecordEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
                result.value = recordEntity
            } catch (e: RecordException) {
                typeError.value = e.typeError
            }
        }
    }
}