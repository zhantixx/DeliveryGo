package com.example.deliverygo.Data

import com.example.deliverygo.Domain.ProductModel

object CartManager {
    private val cartItems = mutableMapOf<ProductModel, Int>()

    fun addToCart(product: ProductModel) {
        cartItems[product] = (cartItems[product] ?: 0) + 1
    }

    fun removeFromCart(product: ProductModel) {
        cartItems.remove(product)
    }

    fun changeQuantity(product: ProductModel, delta: Int) {
        val current = cartItems[product] ?: return
        val newQuantity = current + delta
        if (newQuantity <= 0) {
            cartItems.remove(product)
        } else {
            cartItems[product] = newQuantity
        }
    }

    fun getCart(): Map<ProductModel, Int> = cartItems.toMap()


    fun getTotalPrice(): Double =
        cartItems.entries.sumOf { it.key.price * it.value }

    fun clear() {
        cartItems.clear() // ✅ вот он!
    }
}
