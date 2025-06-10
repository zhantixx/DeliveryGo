package com.example.deliverygo.Domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val price: Double,
    val imageRes: Int,
    val categoryId: Int = 0,
    val rating: Double
) : Parcelable
