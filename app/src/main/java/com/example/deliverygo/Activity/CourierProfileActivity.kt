package com.example.deliverygo.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.databinding.ActivityCourierProfileBinding
import kotlinx.coroutines.launch
import android.widget.EditText
import android.widget.LinearLayout
import com.example.deliverygo.Data.CourierOrderDao
import com.example.deliverygo.Data.UserDao
import com.example.deliverygo.R
import com.example.deliverygo.Adapter.CourierOrderHistoryAdapter // Этот импорт может быть удален, если адаптер нигде не используется

class CourierProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourierProfileBinding
    private lateinit var userDao: UserDao
    private lateinit var courierOrderDao: CourierOrderDao
     private lateinit var historyAdapter: CourierOrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourierProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        userDao = db.userDao()
        courierOrderDao = db.courierOrderDao()

        setupBottomNavigation()
        setupClickListeners()
        // УДАЛЕНО: Вызов метода setupOrderHistoryRecyclerView(), так как RecyclerView истории перенесен
        // setupOrderHistoryRecyclerView()
        loadUserProfile()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationCourierProfile.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home_kur -> { // Корректный ID
                    startActivity(Intent(this, CourierActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_zakaz -> { // Корректный ID
                    startActivity(Intent(this, CourierCurrentOrderActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile_kur -> { // Корректный ID
                    // Уже здесь
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigationCourierProfile.selectedItemId = R.id.navigation_profile_kur
    }


    private fun setupOrderHistoryRecyclerView() {
        historyAdapter = CourierOrderHistoryAdapter(mutableListOf())
        binding.courierOrderHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CourierProfileActivity)
            adapter = historyAdapter
            isNestedScrollingEnabled = false
        }

        courierOrderDao.getOrderHistory().observe(this) { orders ->
            historyAdapter.updateOrders(orders)
            if (orders.isEmpty()) {
                Toast.makeText(this, "Tapsyrys tarıhy bos.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun loadUserProfile() {
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentLoggedInUserId = sharedPrefs.getInt("logged_in_user_id", -1)
        val currentLoggedInUserRole = sharedPrefs.getString("logged_in_user_role", "client")

        if (currentLoggedInUserId == -1 || currentLoggedInUserRole != "courier") {
            Toast.makeText(this, "Kurer retinde kirmegen. Kirý paraǵyna qayta ótińiz.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@CourierProfileActivity, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            val user = userDao.getUserById(currentLoggedInUserId)
            if (user != null && user.role == "courier") {
                binding.courierProfileName.text = user.login
                binding.courierProfileEmail.text = user.email

                binding.courierBalanceDisplay.text = "Teńgerim \n${user.bonusBalance} ₸"

                binding.courierVehicleType.text = user.vehicleType ?: "Anyqtamaý"
                binding.courierDriverLicense.text = user.driverLicenseNumber ?: "Anyqtamaý"
                binding.courierLicensePlate.text = user.licensePlate ?: "Anyqtamaý"
                binding.courierPhoneNumber.text = user.phoneNumber ?: "Anyqtamaý"
                binding.courierLogin.text = user.login
                binding.courierEmail.text = user.email

            } else {
                Toast.makeText(this@CourierProfileActivity, "Pai'dalanushy derekteri tabylmady yemes ku'rier emes.", Toast.LENGTH_LONG).show()
                sharedPrefs.edit().remove("logged_in_user_id").apply()
                sharedPrefs.edit().remove("logged_in_user_role").apply()
                startActivity(Intent(this@CourierProfileActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        // Теперь эта кнопка будет перенаправлять на отдельную Activity истории заказов
        binding.historyCard.setOnClickListener {
            startActivity(Intent(this@CourierProfileActivity, CourierOrderHistoryActivity::class.java))
        }

        binding.exitCard.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Şyǵw")
                .setMessage("Şyqqyńyz keletınıne senımsiz be?")
                .setPositiveButton("İä") { dialog, which ->
                    val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().clear().apply()
                    startActivity(Intent(this@CourierProfileActivity, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Joq", null)
                .show()
        }

        binding.editProfileBtn.setOnClickListener {
            startActivity(Intent(this@CourierProfileActivity, EditCourierProfileActivity::class.java))
        }

        binding.withdrawMoneyBtn.setOnClickListener {
            showWithdrawMoneyDialog()
        }
    }

    private fun showWithdrawMoneyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aqsha shyǵarw")

        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val amountInput = EditText(this).apply {
            hint = "Shyǵaratyn somany engizińiz (tg)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val detailsInput = EditText(this).apply {
            hint = "Bank kartasy / Esep nómiri"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 30 }
        }

        inputLayout.addView(amountInput)
        inputLayout.addView(detailsInput)
        builder.setView(inputLayout)

        builder.setPositiveButton("Shyǵarw") { dialog, _ ->
            val amount = amountInput.text.toString().toIntOrNull() ?: 0
            val details = detailsInput.text.toString().trim()

            if (amount <= 0 || details.isEmpty()) {
                Toast.makeText(this, "Jalǵyz emes soma men derekterdi engizińiz", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            lifecycleScope.launch {
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val currentLoggedInUserId = sharedPrefs.getInt("logged_in_user_id", -1)

                val user = userDao.getUserById(currentLoggedInUserId)
                if (user != null) {
                    if (amount <= user.bonusBalance) {
                        user.bonusBalance -= amount
                        userDao.update(user)

                        Toast.makeText(this@CourierProfileActivity, "$amount tg shyǵaryldy. Kútıńiz...", Toast.LENGTH_LONG).show()

                        loadUserProfile() // Обновляем профиль, чтобы баланс обновился на UI
                    } else {
                        Toast.makeText(this@CourierProfileActivity, "Teńgerim jetkiliksiz", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CourierProfileActivity, "Pai'dalanushy derekteri tabylmady. Qayta kiriniz.", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Bas tartu", null)
        builder.show()
    }
}
