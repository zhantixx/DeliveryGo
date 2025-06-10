package com.example.deliverygo.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // Не забудьте импорт

@Dao
interface CourierOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CourierOrder)

    @Update
    suspend fun updateOrder(order: CourierOrder)

    @Query("SELECT * FROM courier_orders WHERE status = 'available'")
    fun getAvailableOrders(): LiveData<List<CourierOrder>>
    @Query("SELECT * FROM courier_orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: Int): LiveData<CourierOrder?>
    @Query("SELECT * FROM courier_orders WHERE status = 'accepted' LIMIT 1")
    fun getCurrentOrder(): LiveData<CourierOrder?> // LiveData<CourierOrder?> потому что может не быть текущего заказа

    @Query("SELECT * FROM courier_orders WHERE status IN ('delivered', 'cancelled') ORDER BY id DESC")
    fun getOrderHistory(): LiveData<List<CourierOrder>>

    @Query("DELETE FROM courier_orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Int)

    @Query("DELETE FROM courier_orders")
    suspend fun deleteAllOrders() // Для очистки базы
}
