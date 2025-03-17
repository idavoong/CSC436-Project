package com.zybooks.petadoption.data

import java.util.Calendar
import java.util.Date

object FinanceDataSource {
    // Use a mutable list so you can update, add, and remove items.
    private val financeList = mutableListOf(
        Finance(
            id = 1,
            name = "Food",
            category = FinanceType.EXPENSE,
            amount = 5.70,
            date = Calendar.getInstance().apply { set(2025, Calendar.DECEMBER, 4) }
        ),
        Finance(
            id = 2,
            name = "Groceries",
            category = FinanceType.EXPENSE,
            amount = 52.34,
            date = Calendar.getInstance().apply { set(2025, Calendar.DECEMBER, 4) }
        ),
        Finance(
            id = 3,
            name = "Paycheck",
            category = FinanceType.INCOME,
            amount = 1000.29,
            date = Calendar.getInstance().apply { set(2025, Calendar.DECEMBER, 8) }
        )
    )

    fun getFinance(id: Int): Finance? {
        return financeList.find { it.id == id }
    }

    // Return an immutable copy of the list.
    fun loadFinances(): List<Finance> = financeList.toList()

    // Update a finance record by replacing the matching item.
    fun updateFinance(updatedFinance: Finance) {
        val index = financeList.indexOfFirst { it.id == updatedFinance.id }
        if (index != -1) {
            financeList[index] = updatedFinance
        }
    }

    // Delete a finance record.
    fun deleteFinance(finance: Finance) {
        financeList.removeIf { it.id == finance.id }
    }

    // Optionally, add new finance records.
    fun addFinance(finance: Finance) {
        financeList.add(finance)
    }
}