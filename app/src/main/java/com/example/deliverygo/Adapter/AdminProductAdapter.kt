package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.databinding.ItemAdminProductBinding

class AdminProductAdapter(
    private val onEditClick: (ProductModel) -> Unit,
    private val onDeleteClick: (ProductModel) -> Unit
) : ListAdapter<ProductModel, AdminProductAdapter.ProductViewHolder>(DiffCallback()) {

    inner class ProductViewHolder(val binding: ItemAdminProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.titleText.text = product.title
        holder.binding.priceText.text = "${product.price} ₸"


        holder.binding.editBtn.setOnClickListener { onEditClick(product) }
        holder.binding.deleteBtn.setOnClickListener { onDeleteClick(product) }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }
}
