package com.example.moneymaster.ui.exp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymaster.components.DbDriver

class ExpViewModel : ViewModel() {
    private val _Categorys = MutableLiveData<List<String>>().apply {
        value = listOf("LOL")
    }
    val categorys: LiveData<List<String>> = _Categorys
    fun getCategorys(dbHelper: DbDriver.DbHelper){
        _Categorys.value= dbHelper.getAllExpensesCategorysNames()
    }
}