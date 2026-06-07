package com.pdv85.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdv85.app.data.remote.model.Sale
import com.pdv85.app.databinding.FragmentHistoryBinding
import com.pdv85.app.util.Result
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private val vm: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentHistoryBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HistoryAdapter { sale -> confirmCancel(sale) }
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter
        b.swipeRefresh.setOnRefreshListener { vm.load() }

        lifecycleScope.launch {
            vm.sales.collect { state ->
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

    private fun confirmCancel(sale: Sale) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancelar Venda")
            .setMessage("Cancelar venda #${sale.id}?")
            .setPositiveButton("Cancelar venda") { _, _ -> vm.cancel(sale.id) }
            .setNegativeButton("Voltar", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
