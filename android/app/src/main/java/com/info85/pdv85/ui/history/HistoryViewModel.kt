package com.info85.pdv85.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info85.pdv85.data.remote.model.Sale
import com.info85.pdv85.data.repository.SaleRepository
import com.info85.pdv85.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repo = SaleRepository()

    private val _sales = MutableStateFlow<Result<List<Sale>>>(Result.Loading)
    val sales: StateFlow<Result<List<Sale>>> = _sales

    private val _actionResult = MutableStateFlow<Result<Unit>?>(null)
    val actionResult: StateFlow<Result<Unit>?> = _actionResult

    init { load() }

    fun load() {
        viewModelScope.launch {
            _sales.value = Result.Loading
            _sales.value = repo.getSales()
        }
    }

    fun cancel(id: Int) {
        viewModelScope.launch {
            _actionResult.value = repo.cancelSale(id)
            if (_actionResult.value is Result.Success) load()
        }
    }

    fun clearActionResult() { _actionResult.value = null }
}
