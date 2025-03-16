package com.zybooks.petadoption.ui

import androidx.lifecycle.ViewModel
import com.zybooks.petadoption.data.Finance
import com.zybooks.petadoption.data.FinanceDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel : ViewModel() {
    private val _finance = MutableStateFlow(Finance()) // Default empty finance
    val finance: StateFlow<Finance> = _finance.asStateFlow()

    fun loadFinance(id: Int) {
        _finance.value = FinanceDataSource().getFinance(id) ?: Finance()
    }
}