package com.example.deliverygo.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PaymentCardDao {

    @Query("SELECT * FROM payment_cards")
    fun getAll(): LiveData<List<PaymentCard>>

    @Insert
    suspend fun insert(card: PaymentCard)

    @Delete
    suspend fun delete(card: PaymentCard)

    @Query("DELETE FROM payment_cards")
    suspend fun clearAll()
}
