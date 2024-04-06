package com.example.moneymaster.models

class CategoryModel (
    val CategoryName: String,
    val CategoryDescription: String,
    val CategoryIcon: String,
    val CategoryExpenses: Float,
    val CategoryIncome: Float,
) {
    override fun toString(): String {
        return "CategoryModel(CategoryName='$CategoryName', CategoryDescription='$CategoryDescription', CategoryIcon='$CategoryIcon', CategoryExpenses=$CategoryExpenses, CategoryIncome=$CategoryIncome)"
    }
}