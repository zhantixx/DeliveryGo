package com.example.deliverygo.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityLoginBinding
import com.google.gson.Gson // Импортируем Gson
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


data class LoginResponse(
    val status: String,
    val message: String?,
    val user: LoggedInUser?
)

data class LoggedInUser(
    val id: Int,
    val login: String,
    val email: String,
    val role: String,
    val bonusBalance: Int? = null

)


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginEditText: EditText = binding.loginTxt // Используем binding
        val passwordEditText: EditText = binding.passTxt // Используем binding
        val loginButton: Button = binding.logBtn // Используем binding

        binding.regTxt.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString().trim() // Убираем пробелы
            val password = passwordEditText.text.toString().trim() // Убираем пробелы

            if (login.isNotEmpty() && password.isNotEmpty()) {
                loginUser(login, password)
            } else {
                Toast.makeText(this, "Barlıq auqytlar toltyrılmalı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(login: String, password: String) {
        // Логика для админа (если это временное решение, можно оставить)
        if (login == "admin" && password == "123") {
            Toast.makeText(this, "Admin kirisi satti", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminActivity::class.java) // Замените на вашу AdminActivity
            startActivity(intent)
            finish()
            return
        }

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("login", login)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8888/deliverygo/login.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Serverge qosyla almadym", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val gson = Gson()
                            val loginResponse = gson.fromJson(responseBody, LoginResponse::class.java)

                            when (loginResponse.status) {
                                "success" -> {
                                    val user = loginResponse.user
                                    if (user != null) {
                                        Toast.makeText(this@LoginActivity, "Kiru satti!", Toast.LENGTH_SHORT).show()

                                        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
                                            .fallbackToDestructiveMigration()
                                            .build()
                                        val userDao = db.userDao()

                                        lifecycleScope.launch {
                                            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                            sharedPrefs.edit().putInt("logged_in_user_id", user.id).apply()
                                            sharedPrefs.edit().putString("logged_in_user_role", user.role).apply()



                                            userDao.insert(
                                                UserEntity(
                                                    id = user.id,
                                                    login = user.login,
                                                    email = user.email,

                                                    passwordHash = "NotStoredLocally",
                                                    role = user.role,
                                                    bonusBalance = user.bonusBalance ?: 0
                                                )
                                            )

                                            val targetIntent = when (user.role) {
                                                "client" -> Intent(this@LoginActivity, HomeActivity::class.java)
                                                "courier" -> Intent(this@LoginActivity, CourierActivity::class.java)
                                                else -> Intent(this@LoginActivity, HomeActivity::class.java)
                                            }
                                            startActivity(targetIntent)
                                            finish()
                                        }
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Серверден пайдаланушы деректері алынбады", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "error" -> {
                                    Toast.makeText(this@LoginActivity, "Login nemese qupia soz qate: ${loginResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@LoginActivity, "Серверден белгісіз жауап: $responseBody", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Жауапты талдауда қате: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Серверден қате жауап: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}