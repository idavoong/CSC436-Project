package com.zybooks.petadoption.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zybooks.petadoption.data.Finance
import com.zybooks.petadoption.data.FinanceDataSource
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _financeList = MutableStateFlow(FinanceDataSource.loadFinances())
    val financeList: StateFlow<List<Finance>> = _financeList

    private fun refreshList() {
        _financeList.value = FinanceDataSource.loadFinances()
    }

    fun addFinance(newFinance: Finance) {
        FinanceDataSource.addFinance(newFinance)
        refreshList()
    }

    fun updateFinance(updatedFinance: Finance) {
        FinanceDataSource.updateFinance(updatedFinance)
        refreshList()
    }

    fun deleteFinance(finance: Finance) {
        FinanceDataSource.deleteFinance(finance)
        refreshList()
    }
}
