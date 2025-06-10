package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.databinding.ViewholderPopularBinding

class ProductAdapter(
    private var items: MutableList<ProductModel>,
    private val onItemClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ViewholderPopularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = "${item.price.toInt()} tg"
            ratingTxt.text = item.rating.toString()
            Glide.with(pic.context).load(item.imageRes).into(pic)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ProductModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
