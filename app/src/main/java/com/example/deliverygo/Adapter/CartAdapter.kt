package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliverygo.Data.CartManager
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.databinding.ViewholderCartBinding

class CartAdapter(
    private val items: MutableList<Pair<ProductModel, Int>>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, quantity) = items[position]
        val context = holder.itemView.context

        holder.binding.apply {
            textView22.text = product.title
            textView24.text = "$quantity dana"
            textView25.text = "${(product.price * quantity).toInt()} tg"
            textView28.text = quantity.toString()
            Glide.with(imageView11.context).load(product.imageRes).into(imageView11)

            textView27.setOnClickListener {
                CartManager.changeQuantity(product, 1)
                refresh()
            }

            textView26.setOnClickListener {
                CartManager.changeQuantity(product, -1)
                refresh()
            }

            deleteImg.setOnClickListener {
                CartManager.removeFromCart(product)
                refresh()
            }
        }
    }

    private fun refresh() {
        items.clear()
        items.addAll(CartManager.getCart().toList())
        notifyDataSetChanged()
        onUpdate()
    }

    override fun getItemCount(): Int = items.size
}
