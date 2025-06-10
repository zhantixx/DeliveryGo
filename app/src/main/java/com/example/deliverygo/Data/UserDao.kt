package com.example.deliverygo.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users_table WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM users_table WHERE login = :login LIMIT 1") // Добавил для удобства
    suspend fun getUserByLogin(login: String): UserEntity?

    @Update
    suspend fun update(user: UserEntity)

    @Query("DELETE FROM users_table")
    suspend fun deleteAll()
}