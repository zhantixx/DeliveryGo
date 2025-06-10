package com.example.deliverygo.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productTitle: String,
    val productPrice: Double,
    val productImage: Int,
    val orderDate: Long
)
