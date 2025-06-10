package com.example.deliverygo.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.deliverygo.Adapter.CourierOrdersAdapter
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.CourierOrder
import com.example.deliverygo.Data.CourierOrderDao
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityCourierBinding
import kotlinx.coroutines.launch

class CourierActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourierBinding
    private lateinit var courierOrderDao: CourierOrderDao
    private lateinit var ordersAdapter: CourierOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация базы данных Room
        val db = AppDatabase.getDatabase(applicationContext)
        courierOrderDao = db.courierOrderDao()

        setupRecyclerView()
        setupBottomNavigation()
        mockAndLoadOrders() // Загружаем "моковые" заказы

        // Получаем ID пользователя из SharedPreferences (из LoginActivity)
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("logged_in_user_id", -1)
        val userRole = sharedPrefs.getString("logged_in_user_role", "client")

        if (userId == -1 || userRole != "courier") {
            Toast.makeText(this, "Kurer retinde kirmegen. Kirý paraǵyna qayta ótińiz.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    private fun setupRecyclerView() {
        ordersAdapter = CourierOrdersAdapter(mutableListOf()) { order ->
            // Логика при нажатии на кнопку "Принять заказ"
            acceptOrder(order)
        }
        binding.availableOrdersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CourierActivity)
            adapter = ordersAdapter
        }

        // Наблюдаем за доступными заказами из Room
        courierOrderDao.getAvailableOrders().observe(this) { orders ->
            ordersAdapter.updateOrders(orders)
            if (orders.isEmpty()) {
                Toast.makeText(this, "Jańa tapsyrystar joq.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationCourier.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home_kur -> {
                    // Уже на главном экране, просто обновляем или остаемся
                    Toast.makeText(this, "Jańa tapsyrystar", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_zakaz -> {
                    // Переход на экран текущего заказа
                    startActivity(Intent(this, CourierCurrentOrderActivity::class.java))
                    true
                }
                R.id.navigation_profile_kur -> {
                    // Переход на экран профиля курьера
                    startActivity(Intent(this, CourierProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // --- Метод для "моковых" данных ---
    private fun mockAndLoadOrders() {
        lifecycleScope.launch {
            // Очищаем существующие заказы (для удобства отладки при каждом запуске)
            courierOrderDao.deleteAllOrders()

            // Добавляем несколько моковых ДОСТУПНЫХ заказов
            val availableOrders = listOf(
                CourierOrder(
                    serverOrderId = "K1001",
                    pickupAddress = "Restoran 'Däm', Abai k. 10",
                    deliveryAddress = "Qonaq üy 'Shar', Dostyq d. 5",
                    paymentAmount = 2500,
                    distanceKm = 4.5,
                    customerName = "Alibek S.",
                    customerPhone = "+77011234567",
                    orderDetails = "3 Pizza, 2 Cola",
                    status = "available"
                ),
                CourierOrder(
                    serverOrderId = "K1002",
                    pickupAddress = "Supermarket 'Astana', Qabanbai Batyr d. 25",
                    deliveryAddress = "Turǵyn üy, Turan d. 15/1",
                    paymentAmount = 1800,
                    distanceKm = 3.1,
                    customerName = "Madina T.",
                    customerPhone = "+77029876543",
                    orderDetails = "Azyq-túlik (5 kg)",
                    status = "available"
                )
            )

            // Добавляем несколько моковых ЗАВЕРШЕННЫХ заказов для истории
            val historicalOrders = listOf(
                CourierOrder(
                    serverOrderId = "K0001",
                    pickupAddress = "Kafe 'Jibek Joly', Seyfullin k. 5",
                    deliveryAddress = "Turǵyn üy, Altynsarin k. 20",
                    paymentAmount = 1500,
                    distanceKm = 2.0,
                    customerName = "Dina R.",
                    customerPhone = "+77051112233",
                    orderDetails = "Kofe jáne desert",
                    status = "delivered" // ЗАВЕРШЕННЫЙ ЗАКАЗ
                ),
                CourierOrder(
                    serverOrderId = "K0002",
                    pickupAddress = "Gúljahan dúkeni, Baytursynov k. 15",
                    deliveryAddress = "Ofıs ortalyǵy, Manash k. 8",
                    paymentAmount = 2200,
                    distanceKm = 3.8,
                    customerName = "Serik B.",
                    customerPhone = "+77774445566",
                    orderDetails = "Kezdeysok tapsyrys",
                    status = "delivered" // ЗАВЕРШЕННЫЙ ЗАКАЗ
                ),
                CourierOrder(
                    serverOrderId = "K0003",
                    pickupAddress = "Qazaqstan Banki, Respublikalyq d. 30",
                    deliveryAddress = "Jeke úy, Tole Bi k. 50",
                    paymentAmount = 1000,
                    distanceKm = 1.2,
                    customerName = "Laura X.",
                    customerPhone = "+77007778899",
                    orderDetails = "Qujattar",
                    status = "cancelled" // ОТМЕНЕННЫЙ ЗАКАЗ (тоже должен быть в истории)
                )
            )

            // Вставляем все моковые заказы в базу данных
            (availableOrders + historicalOrders).forEach { courierOrderDao.insertOrder(it) }
        }
    }

    // --- Логика принятия заказа ---
    private fun acceptOrder(order: CourierOrder) {
        lifecycleScope.launch {
            order.status = "accepted" // Изменяем статус заказа на "accepted"
            courierOrderDao.updateOrder(order) // Обновляем заказ в Room

            Toast.makeText(this@CourierActivity, "Tapsyrys #${order.serverOrderId} qabyldandy!", Toast.LENGTH_SHORT).show()

            // Теперь переходим на экран текущего заказа, передавая ID заказа
            val intent = Intent(this@CourierActivity, CourierCurrentOrderActivity::class.java).apply {
                putExtra("ORDER_ID", order.id) // Передаем ID заказа в следующую Activity
            }
            startActivity(intent)
        }
    }
}
