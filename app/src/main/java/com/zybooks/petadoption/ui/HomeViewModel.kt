package com.zybooks.petadoption.ui

import androidx.lifecycle.ViewModel
import com.zybooks.petadoption.data.FinanceDataSource

class HomeViewModel : ViewModel() {
    val financeList = FinanceDataSource().loadFinances()
}
