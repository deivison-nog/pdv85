package com.pdv85.app.ui.suppliers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdv85.app.data.remote.model.Supplier
import com.pdv85.app.data.remote.model.SupplierRequest
import com.pdv85.app.data.repository.SupplierRepository
import com.pdv85.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuppliersViewModel : ViewModel() {
    private val repo = SupplierRepository()

    private val _suppliers = MutableStateFlow<Result<List<Supplier>>>(Result.Loading)
    val suppliers: StateFlow<Result<List<Supplier>>> = _suppliers

    private val _actionResult = MutableStateFlow<Result<Unit>?>(null)
    val actionResult: StateFlow<Result<Unit>?> = _actionResult

    init { load() }

    fun load(q: String = "") {
        viewModelScope.launch {
            _suppliers.value = Result.Loading
            _suppliers.value = repo.getSuppliers(q)
        }
    }

    fun save(request: SupplierRequest) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            val r = if (request.id == null) {
                when (val cr = repo.createSupplier(request)) {
                    is Result.Success -> Result.Success(Unit)
                    is Result.Error -> Result.Error(cr.message)
                    else -> Result.Error("Erro")
                }
            } else {
                repo.updateSupplier(request)
            }
            _actionResult.value = r
            if (r is Result.Success) load()
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            _actionResult.value = repo.deleteSupplier(id)
            if (_actionResult.value is Result.Success) load()
        }
    }

    fun clearActionResult() { _actionResult.value = null }
}
