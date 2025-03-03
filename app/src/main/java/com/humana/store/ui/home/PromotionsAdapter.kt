package com.humana.store.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.humana.store.data.model.Promotion
import com.humana.store.data.model.PromotionType
import com.humana.store.databinding.ItemPromotionBinding

class PromotionsAdapter : ListAdapter<Promotion, PromotionsAdapter.PromotionViewHolder>(PromotionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ItemPromotionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PromotionViewHolder(
        private val binding: ItemPromotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(promotion: Promotion) {
            binding.apply {
                titleTextView.text = promotion.title
                descriptionTextView.text = promotion.description

                when (promotion.type) {
                    PromotionType.DISCOUNT -> {
                        typeTextView.text = "${promotion.discountPercentage}% ფასდაკლება"
                    }
                    PromotionType.FIXED_PRICE -> {
                        typeTextView.text = "ფიქსირებული ფასი: ${promotion.fixedPrice}₾"
                    }
                    PromotionType.OPENING -> {
                        typeTextView.text = "ახალი მაღაზია"
                    }
                }

                Glide.with(imageView)
                    .load(promotion.imageUrl)
                    .centerCrop()
                    .into(imageView)
            }
        }
    }

    private class PromotionDiffCallback : DiffUtil.ItemCallback<Promotion>() {
        override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem == newItem
        }
    }
}
