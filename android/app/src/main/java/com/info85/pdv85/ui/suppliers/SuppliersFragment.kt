package com.info85.pdv85.ui.suppliers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.info85.pdv85.data.remote.model.Supplier
import com.info85.pdv85.data.remote.model.SupplierRequest
import com.info85.pdv85.databinding.DialogSupplierFormBinding
import com.info85.pdv85.databinding.FragmentSuppliersBinding
import com.info85.pdv85.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuppliersFragment : Fragment() {

    private var _b: FragmentSuppliersBinding? = null
    private val b get() = _b!!
    private val vm: SuppliersViewModel by viewModels()
    private lateinit var adapter: SuppliersAdapter
    private var searchJob: Job? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSuppliersBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SuppliersAdapter(onEdit = { showForm(it) }, onDelete = { confirmDelete(it) })
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
            vm.suppliers.collect { state ->
                b.swipeRefresh.isRefreshing = state is Result.Loading
                when (state) {
                    is Result.Success -> {
                        adapter.submitList(state.data)
                        b.tvEmpty.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        b.tvError.visibility = View.GONE
                    }
                    is Result.Error -> {
                        b.tvError.text = state.message; b.tvError.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            vm.actionResult.collect { result ->
                if (result is Result.Error) {
                    AlertDialog.Builder(requireContext())
                        .setMessage(result.message).setPositiveButton("OK", null).show()
                    vm.clearActionResult()
                }
            }
        }
    }

    private fun showForm(supplier: Supplier?) {
        val db = DialogSupplierFormBinding.inflate(layoutInflater)
        supplier?.let {
            db.etName.setText(it.name)
            db.etAddress.setText(it.address ?: "")
            db.etDebt.setText(it.debtToSupplier.replace(".", ","))
        }
        AlertDialog.Builder(requireContext())
            .setTitle(if (supplier == null) "Novo Fornecedor" else "Editar Fornecedor")
            .setView(db.root)
            .setPositiveButton("Salvar") { _, _ ->
                vm.save(SupplierRequest(
                    id = supplier?.id,
                    name = db.etName.text.toString().trim(),
                    address = db.etAddress.text.toString().trim().ifEmpty { null },
                    debtToSupplier = db.etDebt.text.toString().replace(",", ".")
                ))
            }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun confirmDelete(supplier: Supplier) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Fornecedor")
            .setMessage("Excluir \"${supplier.name}\"?")
            .setPositiveButton("Excluir") { _, _ -> vm.delete(supplier.id) }
            .setNegativeButton("Cancelar", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
