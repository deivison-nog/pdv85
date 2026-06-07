package com.info85.pdv85.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info85.pdv85.data.remote.model.Category
import com.info85.pdv85.data.remote.model.Product
import com.info85.pdv85.data.remote.model.ProductRequest
import com.info85.pdv85.data.repository.ProductRepository
import com.info85.pdv85.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductsViewModel : ViewModel() {
    private val repo = ProductRepository()

    private val _products = MutableStateFlow<Result<List<Product>>>(Result.Loading)
    val products: StateFlow<Result<List<Product>>> = _products

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _actionResult = MutableStateFlow<Result<Unit>?>(null)
    val actionResult: StateFlow<Result<Unit>?> = _actionResult

    init { loadCategories(); load() }

    fun load(q: String = "") {
        viewModelScope.launch {
            _products.value = Result.Loading
            _products.value = repo.getProducts(q)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val r = repo.getCategories()
            if (r is Result.Success) _categories.value = r.data
        }
    }

    fun save(request: ProductRequest) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            val r = if (request.id == null) {
                when (val cr = repo.createProduct(request)) {
                    is Result.Success -> Result.Success(Unit)
                    is Result.Error -> Result.Error(cr.message)
                    else -> Result.Error("Erro")
                }
            } else {
                repo.updateProduct(request)
            }
            _actionResult.value = r
            if (r is Result.Success) load()
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            _actionResult.value = repo.deleteProduct(id)
            if (_actionResult.value is Result.Success) load()
        }
    }

    fun clearActionResult() { _actionResult.value = null }
}
