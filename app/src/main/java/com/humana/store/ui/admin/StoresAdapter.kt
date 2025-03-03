package com.humana.store.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.humana.store.R
import com.humana.store.data.model.Store
import com.humana.store.databinding.ItemStoreBinding

class StoresAdapter(
    private val onEditClick: (Store) -> Unit,
    private val onDeleteClick: (Store) -> Unit
) : ListAdapter<Store, StoresAdapter.StoreViewHolder>(StoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoreViewHolder(
        private val binding: ItemStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Store) {
            binding.apply {
                storeName.text = store.name
                storeAddress.text = store.address
                storeWorkingHours.text = store.workingHours

                Glide.with(storeImage)
                    .load(store.imageUrl)
                    .placeholder(R.drawable.ic_store)
                    .error(R.drawable.ic_store)
                    .centerCrop()
                    .into(storeImage)

                btnEdit.setOnClickListener { onEditClick(store) }
                btnDelete.setOnClickListener { onDeleteClick(store) }
            }
        }
    }

    private class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }
    }
}
