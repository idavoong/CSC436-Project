package com.zybooks.petadoption.data

import java.util.Date

enum class FinanceType {
    INCOME, EXPENSE
}

data class Finance (
    val id: Int = 0,
    val name: String = "",
    val category: FinanceType = FinanceType.EXPENSE,
    val amount: Double = 0.0,
    val date: Date = Date()
)
