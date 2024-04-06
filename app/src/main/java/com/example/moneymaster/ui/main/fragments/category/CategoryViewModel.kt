package com.example.moneymaster.ui.main.fragments.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.models.CategoryModel

class CategoryViewModel : ViewModel() {

    private val _categorys = MutableLiveData<List<CategoryModel>>().apply {
        value = mutableListOf()
    }
    val categorys: LiveData<List <CategoryModel>> = _categorys
     fun getCategorys(dbHelper: DbDriver.DbHelper){
        _categorys.value=dbHelper.getCategorysSumByAccountID(0)
    }
}