package com.info85.pdv85.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.info85.pdv85.data.remote.model.Client
import com.info85.pdv85.databinding.ItemClientBinding
import com.info85.pdv85.util.toBRL

class ClientsAdapter(
    private val onEdit: (Client) -> Unit,
    private val onDelete: (Client) -> Unit
) : ListAdapter<Client, ClientsAdapter.VH>(DIFF) {

    inner class VH(val b: ItemClientBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemClientBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        with(holder.b) {
            tvName.text = c.name
            tvAddress.text = c.address ?: "—"
            tvDebt.text = (c.debt.toDoubleOrNull() ?: 0.0).toBRL()
            btnEdit.setOnClickListener { onEdit(c) }
            btnDelete.setOnClickListener { onDelete(c) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Client>() {
            override fun areItemsTheSame(a: Client, b: Client) = a.id == b.id
            override fun areContentsTheSame(a: Client, b: Client) = a == b
        }
    }
}
