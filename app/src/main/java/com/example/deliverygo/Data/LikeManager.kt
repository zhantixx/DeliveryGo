package com.example.deliverygo.Data

import com.example.deliverygo.Domain.ProductModel

object LikeManager {
    private val likedItems = mutableListOf<ProductModel>()

    fun addFavorite(product: ProductModel) {
        if (!likedItems.contains(product)) likedItems.add(product)
    }

    fun removeFavorite(product: ProductModel) {
        likedItems.remove(product)
    }

    fun getFavorites(): List<ProductModel> = likedItems
}