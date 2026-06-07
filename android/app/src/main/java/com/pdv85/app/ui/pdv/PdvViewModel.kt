package com.pdv85.app.ui.pdv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdv85.app.data.remote.model.CreateSaleRequest
import com.pdv85.app.data.remote.model.Product
import com.pdv85.app.data.remote.model.SaleItem
import com.pdv85.app.data.remote.model.SaleResponse
import com.pdv85.app.data.repository.ProductRepository
import com.pdv85.app.data.repository.SaleRepository
import com.pdv85.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartEntry(val product: Product, var qty: Int)

class PdvViewModel : ViewModel() {
    private val productRepo = ProductRepository()
    private val saleRepo = SaleRepository()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _cart = MutableStateFlow<List<CartEntry>>(emptyList())
    val cart: StateFlow<List<CartEntry>> = _cart

    private val _saleResult = MutableStateFlow<Result<SaleResponse>?>(null)
    val saleResult: StateFlow<Result<SaleResponse>?> = _saleResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val cartMap = LinkedHashMap<Int, CartEntry>()

    fun search(q: String) {
        if (q.isBlank()) { _searchResults.value = emptyList(); return }
        viewModelScope.launch {
            when (val r = productRepo.getProducts(q, 20)) {
                is Result.Success -> _searchResults.value = r.data
                is Result.Error -> _error.value = r.message
                else -> Unit
            }
        }
    }

    fun addToCart(product: Product) {
        val entry = cartMap.getOrPut(product.id) { CartEntry(product, 0) }
        entry.qty++
        _cart.value = cartMap.values.toList()
    }

    fun increment(productId: Int) {
        cartMap[productId]?.qty = (cartMap[productId]?.qty ?: 0) + 1
        _cart.value = cartMap.values.toList()
    }

    fun decrement(productId: Int) {
        val e = cartMap[productId] ?: return
        e.qty = maxOf(1, e.qty - 1)
        _cart.value = cartMap.values.toList()
    }

    fun removeFromCart(productId: Int) {
        cartMap.remove(productId)
        _cart.value = cartMap.values.toList()
    }

    fun clearCart() {
        cartMap.clear()
        _cart.value = emptyList()
        _searchResults.value = emptyList()
    }

    fun subtotal(): Double = cartMap.values.sumOf { it.product.price.toDoubleOrNull() ?: 0.0 * it.qty }

    fun total(discount: Double): Double = maxOf(0.0, subtotal() - maxOf(0.0, discount))

    fun finalizeSale(payment: String, discount: Double, cashPaid: Double?) {
        val items = cartMap.values.map { SaleItem(it.product.id, it.qty) }
        if (items.isEmpty()) { _error.value = "Carrinho vazio"; return }

        val tot = total(discount)
        val cashChange = if (payment == "DINHEIRO" && cashPaid != null) cashPaid - tot else null
        val request = CreateSaleRequest(
            paymentMethod = payment,
            discountTotal = discount,
            items = items,
            cashPaid = if (payment == "DINHEIRO") cashPaid else null,
            cashChange = if (payment == "DINHEIRO") cashChange else null
        )
        viewModelScope.launch {
            _saleResult.value = Result.Loading
            _saleResult.value = saleRepo.createSale(request)
        }
    }

    fun clearSaleResult() { _saleResult.value = null }
    fun clearError() { _error.value = null }
}
