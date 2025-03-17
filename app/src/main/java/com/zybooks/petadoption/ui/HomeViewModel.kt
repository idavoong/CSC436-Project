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
    private val _financeList = MutableStateFlow(FinanceDataSource().loadFinances())
    val financeList: StateFlow<List<Finance>> = _financeList.asStateFlow()

    fun updateFinance(updatedFinance: Finance) {
        _financeList.value = _financeList.value.map {
            if (it.id == updatedFinance.id) updatedFinance else it
        }
    }

    fun deleteFinance(finance: Finance) {
        _financeList.value = _financeList.value.filter { it.id != finance.id }
    }
}
