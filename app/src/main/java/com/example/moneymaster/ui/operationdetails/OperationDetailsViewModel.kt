package com.example.moneymaster.ui.operationdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.models.OperationModel

class OperationDetailsViewModel() : ViewModel() {
    private val _Operation = MutableLiveData<OperationModel?>().apply {
        value = null
    }
    val operation: LiveData<OperationModel?> = _Operation
    fun getOperation(dbHelper: DbDriver.DbHelper, operationID:Int){
        _Operation.value= dbHelper.getOperationByID(operationID)
    }
}