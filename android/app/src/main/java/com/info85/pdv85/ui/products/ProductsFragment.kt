package com.info85.pdv85.ui.products

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
import com.info85.pdv85.data.remote.model.Category
import com.info85.pdv85.data.remote.model.Product
import com.info85.pdv85.data.remote.model.ProductRequest
import com.info85.pdv85.databinding.DialogProductFormBinding
import com.info85.pdv85.databinding.FragmentProductsBinding
import com.info85.pdv85.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {

    private var _b: FragmentProductsBinding? = null
    private val b get() = _b!!
    private val vm: ProductsViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter
    private var searchJob: Job? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentProductsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ProductsAdapter(
            onEdit = { showForm(it) },
            onDelete = { confirmDelete(it) }
        )
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter

        b.swipeRefresh.setOnRefreshListener { vm.load(b.etSearch.text.toString()) }
        b.fabNew.setOnClickListener { showForm(null) }

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch { delay(300); vm.load(s.toString()) }
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, bef: Int, a: Int) = Unit
        })

        lifecycleScope.launch {
            vm.products.collect { state ->
                b.swipeRefresh.isRefreshing = state is Result.Loading
                when (state) {
                    is Result.Success -> {
                        adapter.submitList(state.data)
                        b.tvEmpty.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        b.tvError.visibility = View.GONE
                    }
                    is Result.Error -> {
                        b.tvError.text = state.message
                        b.tvError.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            vm.actionResult.collect { result ->
                when (result) {
                    is Result.Error -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage(result.message).setPositiveButton("OK", null).show()
                        vm.clearActionResult()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showForm(product: Product?) {
        val categories = vm.categories.value
        val db = DialogProductFormBinding.inflate(layoutInflater)

        val catNames = listOf("(sem categoria)") + categories.map { it.name }
        db.spinnerCategory.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, catNames)

        product?.let {
            db.etName.setText(it.name)
            db.etUpc.setText(it.upc ?: "")
            db.etCost.setText(it.costPrice)
            db.etPrice.setText(it.price)
            db.etStock.setText(it.stock.toString())
            val catIndex = categories.indexOfFirst { c -> c.id == it.categoryId }
            db.spinnerCategory.setSelection(if (catIndex >= 0) catIndex + 1 else 0)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (product == null) "Novo Produto" else "Editar Produto")
            .setView(db.root)
            .setPositiveButton("Salvar") { _, _ ->
                val catIndex = db.spinnerCategory.selectedItemPosition
                val category: Category? = if (catIndex > 0) categories.getOrNull(catIndex - 1) else null
                val req = ProductRequest(
                    id = product?.id,
                    name = db.etName.text.toString().trim(),
                    upc = db.etUpc.text.toString().trim().ifEmpty { null },
                    categoryId = category?.id,
                    costPrice = db.etCost.text.toString().replace(",", "."),
                    price = db.etPrice.text.toString().replace(",", "."),
                    stock = db.etStock.text.toString().toIntOrNull() ?: 0
                )
                vm.save(req)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Produto")
            .setMessage("Excluir \"${product.name}\"?")
            .setPositiveButton("Excluir") { _, _ -> vm.delete(product.id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
