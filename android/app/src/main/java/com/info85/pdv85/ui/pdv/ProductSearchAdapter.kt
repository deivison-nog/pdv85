package com.info85.pdv85.ui.pdv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.info85.pdv85.data.remote.model.Product
import com.info85.pdv85.databinding.ItemProductSearchBinding
import com.info85.pdv85.util.toBRL

class ProductSearchAdapter(
    private val onClick: (Product) -> Unit
) : ListAdapter<Product, ProductSearchAdapter.VH>(DIFF) {

    inner class VH(val b: ItemProductSearchBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProductSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        with(holder.b) {
            tvName.text = p.name
            tvUpc.text = "UPC: ${p.upc ?: "—"} • Estoque: ${p.stock}"
            tvPrice.text = (p.price.toDoubleOrNull() ?: 0.0).toBRL()
            root.setOnClickListener { onClick(p) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
            override fun areContentsTheSame(a: Product, b: Product) = a == b
        }
    }
}
