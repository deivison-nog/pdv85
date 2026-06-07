package com.pdv85.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pdv85.app.databinding.FragmentDashboardBinding
import com.pdv85.app.util.Result
import com.pdv85.app.util.toBRL
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!
    private val vm: DashboardViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentDashboardBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.swipeRefresh.setOnRefreshListener { vm.load() }

        lifecycleScope.launch {
            vm.state.collect { state ->
                b.swipeRefresh.isRefreshing = state is Result.Loading
                when (state) {
                    is Result.Success -> {
                        val d = state.data
                        b.tvSalesToday.text = d.salesToday.toBRL()
                        b.tvSalesMonth.text = d.salesMonth.toBRL()
                        b.tvTotalProducts.text = d.totalProducts.toString()
                        b.tvLowStock.text = "${d.lowStockCount} (≤ ${d.lowStockThreshold})"
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
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
