package com.example.deliverygo.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.CourierOrder
import com.example.deliverygo.Data.CourierOrderDao
import com.example.deliverygo.Data.UserDao
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityCourierCurrentOrderBinding
import kotlinx.coroutines.launch

class CourierCurrentOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourierCurrentOrderBinding
    private var currentOrderIdFromIntent: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourierCurrentOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentOrderIdFromIntent = intent.getIntExtra("ORDER_ID", -1)

        val db = AppDatabase.getDatabase(applicationContext)
        val courierOrderDao = db.courierOrderDao()
        val userDao = db.userDao() // Инициализация UserDao здесь

        setupBottomNavigation()

        if (currentOrderIdFromIntent != -1) {
            lifecycleScope.launch {
                // Используем getOrderById, чтобы получить конкретный заказ
                // Убедитесь, что в CourierOrderDao есть:
                // @Query("SELECT * FROM courier_orders WHERE id = :orderId LIMIT 1")
                // fun getOrderById(orderId: Int): LiveData<CourierOrder?>
                val orderLiveData = courierOrderDao.getOrderById(currentOrderIdFromIntent)

                orderLiveData.observe(this@CourierCurrentOrderActivity) { order ->
                    if (order != null) {
                        binding.currentOrderId.text = "Tapsyrys #${order.serverOrderId}"
                        binding.currentOrderDetailsTextView.text = "Alw adresi: ${order.pickupAddress}\n" +
                                "Jetkızw adresi: ${order.deliveryAddress}\n" +
                                "Tólem: ${order.paymentAmount} tg\n" +
                                "Klient: ${order.customerName} (${order.customerPhone})\n" +
                                "Märtebe: ${order.status}"

                        setupOrderActionButtons(order, courierOrderDao, userDao)

                    } else {
                        binding.currentOrderId.text = "Tapsyrys tabylmady."
                        binding.currentOrderDetailsTextView.text = "Aǵymdaǵy tapsyrys tabylmady."
                        Toast.makeText(this@CourierCurrentOrderActivity, "Aǵymdaǵy tapsyrys tabylmady.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            binding.currentOrderId.text = "Tapsyrys ID joq."
            binding.currentOrderDetailsTextView.text = "Tapsyrys ID joq."
            Toast.makeText(this, "Tapsyrys ID joq.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationCourierCurrent.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home_kur -> {
                    startActivity(Intent(this, CourierActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_zakaz -> {
                    Toast.makeText(this, "Aǵymdaǵy tapsyrys", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profile_kur -> {
                    startActivity(Intent(this, CourierProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigationCourierCurrent.selectedItemId = R.id.navigation_zakaz
    }

    // Pass UserDao as a parameter to this function
    private fun setupOrderActionButtons(order: CourierOrder, courierOrderDao: CourierOrderDao, userDao: UserDao) {
        // Кнопка "Tapsyrys alyndy"
        binding.btnMarkPickedUp.setOnClickListener {
            lifecycleScope.launch {
                order.status = "picked_up"
                courierOrderDao.updateOrder(order)
                Toast.makeText(this@CourierCurrentOrderActivity, "Tapsyrys sáttı alyndy!", Toast.LENGTH_SHORT).show()
                binding.btnMarkPickedUp.isEnabled = false
                binding.btnMarkDelivered.isEnabled = true
            }
        }

        // Кнопка "Tapsyrys jetkızıldı"
        binding.btnMarkDelivered.setOnClickListener {
            lifecycleScope.launch {
                order.status = "delivered"
                courierOrderDao.updateOrder(order)

                // Получаем ID залогиненного пользователя из SharedPreferences
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val currentLoggedInUserId = sharedPrefs.getInt("logged_in_user_id", -1)

                if (currentLoggedInUserId != -1) {
                    val user = userDao.getUserById(currentLoggedInUserId)
                    if (user != null) {
                        user.bonusBalance += order.paymentAmount // Пополняем баланс курьера
                        userDao.update(user) // Обновляем пользователя в Room
                        Toast.makeText(this@CourierCurrentOrderActivity, "Balans ${order.paymentAmount} tg-ǵa toltyryldy!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CourierCurrentOrderActivity, "Pai'dalanushy tabylmady. Balans tolyqturylmady.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@CourierCurrentOrderActivity, "Pai'dalanushy ID joq. Balans tolyqturylmady.", Toast.LENGTH_LONG).show()
                }

                Toast.makeText(this@CourierCurrentOrderActivity, "Tapsyrys sáttı jetkızıldı!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CourierCurrentOrderActivity, CourierActivity::class.java))
                finish()
            }
        }

        // Кнопка "Klientke qonyraw shalw"
        binding.btnCallCustomer.setOnClickListener {
            val phoneNumber = order.customerPhone
            if (phoneNumber.isNotEmpty()) {
                // val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                // startActivity(dialIntent)
                Toast.makeText(this@CourierCurrentOrderActivity, "Klientke qonyraw shalw: $phoneNumber", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CourierCurrentOrderActivity, "Klient nomırı joq", Toast.LENGTH_SHORT).show()
            }
        }

        // Инициализация состояния кнопок в зависимости от текущего статуса
        when (order.status) {
            "available", "accepted" -> {
                binding.btnMarkPickedUp.isEnabled = true
                binding.btnMarkDelivered.isEnabled = false
            }
            "picked_up" -> {
                binding.btnMarkPickedUp.isEnabled = false
                binding.btnMarkDelivered.isEnabled = true
            }
            "delivered", "cancelled" -> {
                binding.btnMarkPickedUp.isEnabled = false
                binding.btnMarkDelivered.isEnabled = false
                Toast.makeText(this, "Bul tapsyrys ayqtalǵan nemese joıyldy.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
