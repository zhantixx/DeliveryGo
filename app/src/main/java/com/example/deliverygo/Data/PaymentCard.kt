package com.example.deliverygo.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_cards")
data class PaymentCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String,
    val expiry: String,
    val cvv: String,
    val holder: String
)
