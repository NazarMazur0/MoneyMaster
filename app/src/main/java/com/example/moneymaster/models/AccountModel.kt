package com.example.moneymaster.models

class AccountModel (
    val  AccountID: Int,
    val CurrencyID: Int,
    val Saldo: Float
) {
    override fun toString(): String {
        return "AccountModel(AccountID=$AccountID, CurrencyID=$CurrencyID, Saldo=$Saldo)"
    }
}