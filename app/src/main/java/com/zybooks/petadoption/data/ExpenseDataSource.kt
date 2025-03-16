package com.zybooks.petadoption.data

import java.util.Date

class FinanceDataSource {
    private val financeList = listOf(
        Finance(
            id = 1,
            name = "Food",
            category = FinanceType.EXPENSE,
            amount = 5.70,
            date = Date(2025, 12, 4)
        ),
        Finance(
            id = 2,
            name = "Groceries",
            category = FinanceType.EXPENSE,
            amount = 52.34,
            date = Date(2025, 12, 4)
        ),
        Finance(
            id = 3,
            name = "Paycheck",
            category = FinanceType.INCOME,
            amount = 1000.29,
            date = Date(2025, 12, 8)
        )
    )

    fun getFinance(id: Int): Finance? {
        return financeList.find { it.id == id}
    }

    fun loadFinances() = financeList
}