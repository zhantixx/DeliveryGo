package com.example.deliverygo.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "bonus_balance")
    var bonusBalance: Int = 0,

    @ColumnInfo(name = "vehicle_type") val vehicleType: String? = null,
    @ColumnInfo(name = "license_plate") val licensePlate: String? = null,
    @ColumnInfo(name = "driver_license_number") val driverLicenseNumber: String? = null,
    @ColumnInfo(name = "phone_number") val phoneNumber: String? = null
)