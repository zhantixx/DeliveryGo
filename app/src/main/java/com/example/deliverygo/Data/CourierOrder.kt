package com.example.deliverygo.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courier_orders")
data class CourierOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serverOrderId: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val paymentAmount: Int,
    val distanceKm: Double,
    val customerName: String,
    val customerPhone: String,
    val orderDetails: String,
    var status: String
)
