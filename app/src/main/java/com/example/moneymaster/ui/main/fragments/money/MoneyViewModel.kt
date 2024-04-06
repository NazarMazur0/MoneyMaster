package com.example.moneymaster.ui.main.fragments.money

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.models.OperationModel

class MoneyViewModel : ViewModel() {

    private val _Saldo = MutableLiveData<String>().apply {
        value = "no saldo"
    }
    val saldo: LiveData<String> = _Saldo
    private val _Symbol = MutableLiveData<String>().apply {
        value = "gg"
    }
    val symbol: LiveData<String> = _Symbol
    private val _Operations = MutableLiveData< List<OperationModel>>()

    val operations: LiveData<List<OperationModel>> = _Operations





    fun getOperations(dbHelper: DbDriver.DbHelper) {
        _Operations.value = dbHelper.getOperationsByAccountID(0)
    }
    fun getSaldo(dbHelper: DbDriver.DbHelper){
        Log.d("Saldo","${dbHelper.getAccountSaldoById(0)}")
        _Saldo.value = dbHelper.getAccountSaldoById(0).toString()
    }
    fun getSymbol(dbHelper: DbDriver.DbHelper){
        _Symbol.value = dbHelper.getAccountSymbolById(0)
    }
}