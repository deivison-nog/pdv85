package com.info85.pdv85.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.info85.pdv85.data.remote.model.Sale
import com.info85.pdv85.databinding.ItemSaleBinding
import com.info85.pdv85.util.toBRL

class HistoryAdapter(
    private val onCancel: (Sale) -> Unit
) : ListAdapter<Sale, HistoryAdapter.VH>(DIFF) {

    inner class VH(val b: ItemSaleBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        with(holder.b) {
            tvId.text = "#${s.id}"
            tvDate.text = s.createdAt
            tvPayment.text = s.paymentMethod
            tvStatus.text = s.status
            tvTotal.text = (s.total.toDoubleOrNull() ?: 0.0).toBRL()
            btnCancel.isEnabled = s.status != "CANCELADA"
            btnCancel.setOnClickListener { onCancel(s) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Sale>() {
            override fun areItemsTheSame(a: Sale, b: Sale) = a.id == b.id
            override fun areContentsTheSame(a: Sale, b: Sale) = a == b
        }
    }
}
