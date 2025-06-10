package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.databinding.ActivityRegistrationBinding
import com.google.gson.Gson // Добавьте эту зависимость
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

// Добавьте в build.gradle (Module: app) в секцию dependencies:
// implementation 'com.squareup.okhttp3:okhttp:4.9.3' // Если еще нет
// implementation 'com.google.code.gson:gson:2.8.9' // Для парсинга JSON

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val client = OkHttpClient()
    private lateinit var db: AppDatabase // Объявляем здесь, чтобы использовать везде

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация базы данных Room
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()

        binding.logTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Обработчик для перехода на страницу регистрации курьера
        binding.kurTxt.setOnClickListener {
            val intent = Intent(this, RegistrationKurActivity::class.java)
            startActivity(intent)
        }


        binding.regBtn.setOnClickListener {
            val login = binding.loginTxt.text.toString().trim()
            val email = binding.emailTxt.text.toString().trim()
            val parol = binding.passTxt.text.toString().trim()
            val qaytalauParol = binding.passTxt2.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || parol.isEmpty() || qaytalauParol.isEmpty()) {
                Toast.makeText(this, "Barlyq óristi toltyryńyz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (parol != qaytalauParol) {
                Toast.makeText(this, "Qúpiya sózder sáıkes emes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Вызываем функцию для регистрации клиента
            tirkeuJurgizuClient(login, email, parol)
        }
    }

    // Функция для регистрации КЛИЕНТА
    private fun tirkeuJurgizuClient(login: String, email: String, parol: String) {
        val requestBody = FormBody.Builder()
            .add("login", login)
            .add("email", email)
            .add("password", parol)
            .add("is_courier", "false") // Явно указываем, что это не курьер
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8888/deliverygo/register.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistrationActivity, "Serverge qosyla almadym", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val gson = Gson()
                            val serverResponse = gson.fromJson(responseBody, ServerResponse::class.java)

                            when (serverResponse.status) {
                                "success" -> {
                                    Toast.makeText(this@RegistrationActivity, "Tirkelý satti!", Toast.LENGTH_SHORT).show()

                                    val userDao = db.userDao()
                                    lifecycleScope.launch {
                                        userDao.insert(
                                            UserEntity(
                                                id = serverResponse.user?.id ?: 0,
                                                login = serverResponse.user?.login ?: login,
                                                email = serverResponse.user?.email ?: email,
                                                passwordHash = parol,
                                                role = serverResponse.user?.role ?: "client",
                                                bonusBalance = 0
                                            )
                                        )
                                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                }
                                "already_exists" -> {
                                    Toast.makeText(this@RegistrationActivity, "Búl login nemese email búrın tirkelgen", Toast.LENGTH_SHORT).show()
                                }
                                "missing_fields" -> {
                                    Toast.makeText(this@RegistrationActivity, "Serverge barlyq mindetti óristi berilmedi", Toast.LENGTH_SHORT).show()
                                }
                                "error" -> {
                                    Toast.makeText(this@RegistrationActivity, "Server qatesi: ${serverResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@RegistrationActivity, "Serverden belgisiz jauap: $responseBody", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@RegistrationActivity, "Jauap parsında qate: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@RegistrationActivity, "Serverden qate jauap: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Класс для парсинга JSON-ответа от сервера
    data class ServerResponse(
        val status: String,
        val message: String?,
        val role: String?,
        val user: UserData? // Может быть null, если статус не "success"
    )

    data class UserData(
        val id: Int,
        val login: String,
        val email: String,
        val role: String
        // Добавьте другие поля, если они приходят в ответе и нужны здесь
    )
}