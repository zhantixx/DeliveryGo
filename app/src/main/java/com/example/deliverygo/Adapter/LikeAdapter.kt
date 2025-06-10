package com.example.deliverygo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.R

class LikeAdapter(
    private val favorites: MutableList<ProductModel>,
    private val onItemClick: (ProductModel) -> Unit,
    private val onDeleteClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<LikeAdapter.FavViewHolder>() {

    inner class FavViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.favImage)
        val title: TextView = view.findViewById(R.id.favTitle)
        val price: TextView = view.findViewById(R.id.favPrice)
        val rating: TextView = view.findViewById(R.id.favRating)
        val delete: ImageView = view.findViewById(R.id.deleteFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_favorite, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val product = favorites[position]

        Glide.with(holder.itemView.context).load(product.imageRes).into(holder.image)
        holder.title.text = product.title
        holder.price.text = "${product.price} tg"
        holder.rating.text = "Rating: ${product.rating}"

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }


        holder.delete.setOnClickListener {
            onDeleteClick(product)
        }
    }

    override fun getItemCount(): Int = favorites.size


}
