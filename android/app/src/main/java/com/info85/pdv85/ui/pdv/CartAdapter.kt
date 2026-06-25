package com.info85.pdv85.ui.pdv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.info85.pdv85.databinding.ItemCartBinding
import com.info85.pdv85.util.toBRL

class CartAdapter(
    private val onIncrement: (CartEntry) -> Unit,
    private val onDecrement: (CartEntry) -> Unit,
    private val onRemove: (CartEntry) -> Unit
) : ListAdapter<CartEntry, CartAdapter.VH>(DIFF) {

    inner class VH(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.b) {
            tvName.text = item.product.name
            tvUpc.text = item.product.upc ?: ""
            tvQty.text = item.qty.toString()
            val price = item.product.price.toDoubleOrNull() ?: 0.0
            tvLineTotal.text = (price * item.qty).toBRL()
            btnInc.setOnClickListener { onIncrement(item) }
            btnDec.setOnClickListener { onDecrement(item) }
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CartEntry>() {
            override fun areItemsTheSame(a: CartEntry, b: CartEntry) = a.product.id == b.product.id
            override fun areContentsTheSame(a: CartEntry, b: CartEntry) = a == b
        }
    }
}
