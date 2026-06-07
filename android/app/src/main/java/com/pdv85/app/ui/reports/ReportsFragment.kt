package com.pdv85.app.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pdv85.app.databinding.FragmentReportsBinding
import com.pdv85.app.util.Result
import com.pdv85.app.util.toBRL
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    private var _b: FragmentReportsBinding? = null
    private val b get() = _b!!
    private val vm: ReportsViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentReportsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val today = vm.today
        b.etFrom.setText(today)
        b.etTo.setText(today)

        b.btnGenerate.setOnClickListener {
            val from = b.etFrom.text.toString().trim()
            val to = b.etTo.text.toString().trim()
            if (from.isEmpty() || to.isEmpty()) return@setOnClickListener
            vm.load(from, to)
        }

        lifecycleScope.launch {
            vm.report.collect { state ->
                b.progressBar.visibility = if (state is Result.Loading) View.VISIBLE else View.GONE
                when (state) {
                    is Result.Success -> {
                        val d = state.data
                        b.tvTotalSales.text = d.totalSales.toBRL()
                        b.tvProfit.text = d.profitNet.toBRL()
                        b.tvDiscount.text = d.totalDiscount.toBRL()
                        b.tvTopProducts.text = d.topProducts.joinToString("\n") {
                            "${it.name}: ${it.profitNet.toBRL()}"
                        }
                        b.tvError.visibility = View.GONE
                        b.resultGroup.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        b.tvError.text = state.message
                        b.tvError.visibility = View.VISIBLE
                        b.resultGroup.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
