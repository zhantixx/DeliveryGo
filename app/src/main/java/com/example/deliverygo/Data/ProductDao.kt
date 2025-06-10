package com.example.deliverygo.Data

import androidx.room.*
import com.example.deliverygo.Domain.ProductModel

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductModel>

    @Insert
    suspend fun insert(product: ProductModel)

    @Update
    suspend fun update(product: ProductModel)

    @Delete
    suspend fun delete(product: ProductModel)
}
