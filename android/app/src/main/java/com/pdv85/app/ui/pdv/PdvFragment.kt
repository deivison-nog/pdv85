package com.pdv85.app.ui.pdv

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdv85.app.R
import com.pdv85.app.databinding.DialogCashPaymentBinding
import com.pdv85.app.databinding.FragmentPdvBinding
import com.pdv85.app.util.Result
import com.pdv85.app.util.toBRL
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PdvFragment : Fragment() {

    private var _b: FragmentPdvBinding? = null
    private val b get() = _b!!
    private val vm: PdvViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var searchAdapter: ProductSearchAdapter
    private var searchJob: Job? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentPdvBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cartAdapter = CartAdapter(
            onIncrement = { vm.increment(it.product.id) },
            onDecrement = { vm.decrement(it.product.id) },
            onRemove = { vm.removeFromCart(it.product.id) }
        )
        searchAdapter = ProductSearchAdapter { product ->
            vm.addToCart(product)
            b.etSearch.setText("")
            b.rvSearch.visibility = View.GONE
        }

        b.rvCart.layoutManager = LinearLayoutManager(requireContext())
        b.rvCart.adapter = cartAdapter
        b.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        b.rvSearch.adapter = searchAdapter

        // Payment method spinner
        val payments = listOf("DINHEIRO", "PIX", "DEBITO", "CREDITO")
        b.spinnerPayment.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, payments)
        b.spinnerPayment.setSelection(1) // default PIX

        // Search with debounce
        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString()?.trim() ?: ""
                searchJob?.cancel()
                if (q.isEmpty()) { b.rvSearch.visibility = View.GONE; return }
                searchJob = lifecycleScope.launch {
                    delay(200)
                    vm.search(q)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, a: Int) = Unit
        })

        // Enter key: exact UPC match
        b.etSearch.setOnEditorActionListener { _, _, _ ->
            val q = b.etSearch.text.toString().trim()
            if (q.isNotEmpty() && q.all { it.isDigit() }) {
                val exact = searchAdapter.currentList.find { it.upc == q }
                if (exact != null) {
                    vm.addToCart(exact)
                    b.etSearch.setText("")
                    b.rvSearch.visibility = View.GONE
                }
            }
            true
        }

        b.btnFinalize.setOnClickListener { onFinalize() }

        lifecycleScope.launch {
            vm.searchResults.collect { results ->
                searchAdapter.submitList(results)
                b.rvSearch.visibility = if (results.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            vm.cart.collect { entries ->
                cartAdapter.submitList(entries.toList())
                updateTotals()
                b.btnFinalize.isEnabled = entries.isNotEmpty()
            }
        }

        lifecycleScope.launch {
            vm.saleResult.collect { result ->
                when (result) {
                    is Result.Loading -> b.btnFinalize.isEnabled = false
                    is Result.Success -> {
                        vm.clearCart()
                        vm.clearSaleResult()
                        showSuccess("Venda #${result.data.saleId} finalizada!")
                    }
                    is Result.Error -> {
                        b.btnFinalize.isEnabled = true
                        showError(result.message)
                        vm.clearSaleResult()
                    }
                    null -> Unit
                }
            }
        }

        lifecycleScope.launch {
            vm.error.collect { msg ->
                if (msg != null) { showError(msg); vm.clearError() }
            }
        }
    }

    private fun updateTotals() {
        val discount = b.etDiscount.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0
        b.tvSubtotal.text = vm.subtotal().toBRL()
        b.tvTotal.text = vm.total(discount).toBRL()
    }

    private fun onFinalize() {
        val payment = b.spinnerPayment.selectedItem as String
        val discount = b.etDiscount.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0
        val total = vm.total(discount)

        if (payment == "DINHEIRO") {
            showCashDialog(total, discount)
        } else {
            vm.finalizeSale(payment, discount, null)
        }
    }

    private fun showCashDialog(total: Double, discount: Double) {
        val db = DialogCashPaymentBinding.inflate(layoutInflater)
        db.tvTotal.text = total.toBRL()
        db.etPaid.setText(total.toBRL())

        db.etPaid.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val paid = s?.toString()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                db.tvChange.text = maxOf(0.0, paid - total).toBRL()
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, bef: Int, a: Int) = Unit
        })

        AlertDialog.Builder(requireContext())
            .setTitle("Pagamento em Dinheiro")
            .setView(db.root)
            .setPositiveButton("Confirmar") { _, _ ->
                val paid = db.etPaid.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0
                if (paid < total) {
                    showError("Valor pago menor que o total.")
                    return@setPositiveButton
                }
                vm.finalizeSale("DINHEIRO", discount, paid)
            }
            .setNegativeButton("Voltar", null)
            .show()
    }

    private fun showError(msg: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Erro")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showSuccess(msg: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Sucesso")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
