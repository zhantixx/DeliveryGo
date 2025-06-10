package com.example.deliverygo.Data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deliverygo.Domain.ProductModel

@Database(entities = [OrderEntity::class, PaymentCard::class, ProductModel::class,UserEntity::class,CourierOrder::class], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun cardDao(): PaymentCardDao
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun courierOrderDao(): CourierOrderDao




    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wallet_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
