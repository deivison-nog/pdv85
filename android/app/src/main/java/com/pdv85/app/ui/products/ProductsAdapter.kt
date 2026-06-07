package com.pdv85.app.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pdv85.app.data.remote.model.Product
import com.pdv85.app.databinding.ItemProductBinding
import com.pdv85.app.util.toBRL

class ProductsAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : ListAdapter<Product, ProductsAdapter.VH>(DIFF) {

    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        with(holder.b) {
            tvName.text = p.name
            tvUpc.text = p.upc ?: "—"
            tvCategory.text = p.category ?: "—"
            val cost = p.costPrice.toDoubleOrNull() ?: 0.0
            val price = p.price.toDoubleOrNull() ?: 0.0
            tvCost.text = cost.toBRL()
            tvPrice.text = price.toBRL()
            val pct = if (cost > 0) ((price - cost) / cost) * 100 else null
            tvGain.text = if (pct != null) "%.1f%%".format(pct) else "—"
            tvStock.text = p.stock.toString()
            btnEdit.setOnClickListener { onEdit(p) }
            btnDelete.setOnClickListener { onDelete(p) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
            override fun areContentsTheSame(a: Product, b: Product) = a == b
        }
    }
}
