package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.databinding.ActivityRegistrationKurBinding // Обратите внимание на новый binding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class RegistrationKurActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationKurBinding // Используем новый binding
    private val client = OkHttpClient()
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationKurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()

        binding.logTxtKur.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Обработчик для перехода на страницу регистрации клиента
        binding.clientRegTxtKur.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish() // Закрываем текущую Activity, чтобы не возвращаться по Back
        }

        binding.regKurBtn.setOnClickListener {
            val login = binding.loginTxtKur.text.toString().trim()
            val email = binding.emailTxtKur.text.toString().trim()
            val parol = binding.passTxtKur.text.toString().trim()
            val qaytalauParol = binding.passTxt2Kur.text.toString().trim()

            val vehicleType = binding.vehicleTypeTxtKur.text.toString().trim()
            val licensePlate = binding.licensePlateTxtKur.text.toString().trim()
            val driverLicenseNumber = binding.driverLicenseNumberTxtKur.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || parol.isEmpty() || qaytalauParol.isEmpty() ||
                vehicleType.isEmpty() || licensePlate.isEmpty() || driverLicenseNumber.isEmpty()
            ) {
                Toast.makeText(this, "Barlyq óristi toltyryńyz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (parol != qaytalauParol) {
                Toast.makeText(this, "Qúpiya sózder sáıkes emes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Вызываем функцию для регистрации курьера
            tirkeuJurgizuCourier(login, email, parol, vehicleType, licensePlate, driverLicenseNumber)
        }
    }

    // Функция для регистрации КУРЬЕРА
    private fun tirkeuJurgizuCourier(
        login: String,
        email: String,
        parol: String,
        vehicleType: String,
        licensePlate: String,
        driverLicenseNumber: String
    ) {
        val requestBody = FormBody.Builder()
            .add("login", login)
            .add("email", email)
            .add("password", parol)
            .add("is_courier", "true") // Явно указываем, что это курьер
            .add("vehicle_type", vehicleType)
            .add("license_plate", licensePlate)
            .add("driver_license_number", driverLicenseNumber)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8888/deliverygo/register.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistrationKurActivity, "Serverge qosyla almadym", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val gson = Gson()
                            val serverResponse = gson.fromJson(responseBody, RegistrationActivity.ServerResponse::class.java) // Используем тот же класс

                            when (serverResponse.status) {
                                "success" -> {
                                    Toast.makeText(this@RegistrationKurActivity, "Kuryer tirkelýi satti!", Toast.LENGTH_SHORT).show()

                                    val userDao = db.userDao()
                                    lifecycleScope.launch {
                                        userDao.insert(
                                            UserEntity(
                                                id = serverResponse.user?.id ?: 0,
                                                login = serverResponse.user?.login ?: login,
                                                email = serverResponse.user?.email ?: email,
                                                passwordHash = parol,
                                                role = serverResponse.user?.role ?: "courier",
                                                bonusBalance = 0
                                            )
                                        )
                                        startActivity(Intent(this@RegistrationKurActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                }
                                "already_exists" -> {
                                    Toast.makeText(this@RegistrationKurActivity, "Búl login nemese email búrın tirkelgen", Toast.LENGTH_SHORT).show()
                                }
                                "missing_fields" -> {
                                    Toast.makeText(this@RegistrationKurActivity, "Serverge barlyq mindetti óristi berilmedi", Toast.LENGTH_SHORT).show()
                                }
                                "error" -> {
                                    Toast.makeText(this@RegistrationKurActivity, "Server qatesi: ${serverResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@RegistrationKurActivity, "Serverden belgisiz jauap: $responseBody", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@RegistrationKurActivity, "Jauap parsında qate: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@RegistrationKurActivity, "Serverden qate jauap: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}