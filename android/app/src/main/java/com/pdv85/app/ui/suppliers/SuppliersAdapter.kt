package com.pdv85.app.ui.suppliers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pdv85.app.data.remote.model.Supplier
import com.pdv85.app.databinding.ItemSupplierBinding
import com.pdv85.app.util.toBRL

class SuppliersAdapter(
    private val onEdit: (Supplier) -> Unit,
    private val onDelete: (Supplier) -> Unit
) : ListAdapter<Supplier, SuppliersAdapter.VH>(DIFF) {

    inner class VH(val b: ItemSupplierBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        with(holder.b) {
            tvName.text = s.name
            tvAddress.text = s.address ?: "—"
            tvDebt.text = (s.debtToSupplier.toDoubleOrNull() ?: 0.0).toBRL()
            btnEdit.setOnClickListener { onEdit(s) }
            btnDelete.setOnClickListener { onDelete(s) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Supplier>() {
            override fun areItemsTheSame(a: Supplier, b: Supplier) = a.id == b.id
            override fun areContentsTheSame(a: Supplier, b: Supplier) = a == b
        }
    }
}
