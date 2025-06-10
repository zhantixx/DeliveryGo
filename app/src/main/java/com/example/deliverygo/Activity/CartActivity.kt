package com.example.deliverygo.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.deliverygo.Adapter.CartAdapter
import com.example.deliverygo.Data.* // Убедитесь, что все необходимые классы импортированы
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityCartBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var dao: PaymentCardDao
    private var walletPopupWindow: PopupWindow? = null
    private var appliedBonus: Int = 0

    // TODO: ВНИМАНИЕ! Этот ID должен приходить от вашей системы аутентификации после входа пользователя.
    //  Не используйте хардкодный ID=1 в реальном приложении!
    //  Его следует получать, например, из SharedPreferences, где он сохраняется после успешного логина.
    private var currentLoggedInUserId: Int = 1 // ЗАМЕНИТЕ НА АКТУАЛЬНЫЙ ID ЗАЛОГИНЕННОГО ПОЛЬЗОВАТЕЛЯ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCart()

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()

        dao = db.cardDao()

        // Инициализация DAO для UserEntity
        val userDao = db.userDao()

        binding.sebetBtn2.setOnClickListener {
            showDeliveryTypeDialog()
        }
        binding.walletImg.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({ showWalletPopup() }, 100)
        }

        binding.fastImg.setOnClickListener {
            showOrderPopup()
        }
        binding.timeImg.setOnClickListener {
            showDateTimePicker()
        }

        // Обработчик для кнопки бонусов (если она есть на UI, то вызываем диалог ввода бонусов)
        // Если вы хотите, чтобы диалог ввода бонусов вызывался, например, при нажатии на текст с бонусами
        // или другой элемент, подключите его здесь.
        // Пример (добавьте соответствующий ID в ваш XML, если его нет):
        // binding.bonusButtonId.setOnClickListener { // Замените bonusButtonId на реальный ID
        //     showBonusInputDialog(userDao)
        // }


        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_like -> {
                    startActivity(Intent(this, LikeActivity::class.java))
                    true
                }
                R.id.navigation_cart -> true
                R.id.navigation_story -> {
                    startActivity(Intent(this, StoryActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showBonusInputDialog(userDao: UserDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Бонусты енгізіңіз")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("Koldanu") { dialog, _ ->
            val entered = input.text.toString().toIntOrNull() ?: 0

            lifecycleScope.launch {
                val user = userDao.getUserById(currentLoggedInUserId) // Используем актуальный ID
                if (user != null && entered <= user.bonusBalance) {
                    appliedBonus = entered
                    updateTotals()
                    Toast.makeText(this@CartActivity, "$entered бонус қолданылды", Toast.LENGTH_SHORT).show()
                } else if (user == null) {
                    Toast.makeText(this@CartActivity, "Пайдаланушы табылмады. Қайта кіріңіз.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CartActivity, "Бонустар жеткіліксіз", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Bas tartu") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setupCart() {
        cartAdapter = CartAdapter(CartManager.getCart().toList().toMutableList()) {
            updateTotals()
        }
        binding.cartView.layoutManager = LinearLayoutManager(this)
        binding.cartView.adapter = cartAdapter
        updateTotals()
    }

    private fun updateTotals() {
        val cartMap = CartManager.getCart()
        val totalItems = cartMap.values.sum()
        val itemsTotal = cartMap.entries.sumOf { it.key.price * it.value }.toInt()
        val deliveryFee = 1000
        var finalTotal = itemsTotal + deliveryFee - appliedBonus
        if (finalTotal < 0) finalTotal = 0

        binding.textView16.text = "$totalItems"
        binding.textView20.text = "$itemsTotal tg"
        binding.textView18.text = "$deliveryFee tg"
        binding.textView23.text = "$finalTotal tg"
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                showOrderPopup()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showDeliveryTypeDialog() {
        val options = arrayOf("Jyldam jetkızu", "Künı men uaqyty boiynşa jetkızu")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Jetkızu türın tañdañyz")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showOrderPopup()
                1 -> showDateTimePicker()
            }
        }
        builder.show()
    }

    private fun showOrderPopup() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.popup_order_form, null)
        val popup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val name = view.findViewById<EditText>(R.id.etName)
        val phone = view.findViewById<EditText>(R.id.etPhone)
        val address = view.findViewById<EditText>(R.id.etAddress)
        val comment = view.findViewById<EditText>(R.id.etComment)
        val bonusCheckbox = view.findViewById<CheckBox>(R.id.checkboxUseBonus)
        val bonusInput = view.findViewById<EditText>(R.id.etBonusInput)
        val submitBtn = view.findViewById<Button>(R.id.btnSubmitOrder)

        bonusInput.visibility = View.GONE

        bonusCheckbox.setOnCheckedChangeListener { _, isChecked ->
            bonusInput.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        submitBtn.setOnClickListener {
            val enteredBonus = bonusInput.text.toString().toIntOrNull() ?: 0

            if (name.text.isNotEmpty() && phone.text.isNotEmpty() && address.text.isNotEmpty()) {
                // НЕ закрываем popup сразу, если есть логика с бонусами, которая может вернуть ошибку.
                // popup.dismiss() // Это может быть вызвано позже, после успешного выполнения всей логики

                Toast.makeText(this, "Tapsyrys qabyldandy!", Toast.LENGTH_LONG).show()

                val db = AppDatabase.getDatabase(applicationContext)
                val orderDao = db.orderDao()
                val userDao = db.userDao() // Получаем DAO для UserEntity

                val cart = CartManager.getCart()
                val deliveryFee = 1000
                val totalPrice = cart.entries.sumOf { it.key.price * it.value } + deliveryFee
                val bonusEarned = (totalPrice * 0.05).toInt()
                var totalWithBonus = totalPrice

                lifecycleScope.launch {
                    val user = userDao.getUserById(currentLoggedInUserId) // Используем актуальный ID

                    if (bonusCheckbox.isChecked) {
                        if (user != null) {
                            if (enteredBonus <= user.bonusBalance) {
                                totalWithBonus -= enteredBonus
                                user.bonusBalance -= enteredBonus
                                user.bonusBalance += bonusEarned
                                userDao.update(user) // Вызов update на UserEntity, который теперь содержит role
                                // Только если все успешно, закрываем popup
                                popup.dismiss()
                            } else {
                                Toast.makeText(this@CartActivity, "Бонустар жеткіліксіз", Toast.LENGTH_SHORT).show()
                                return@launch // Выходим из корутины, если бонусов недостаточно
                            }
                        } else {
                            Toast.makeText(this@CartActivity, "Пайдаланушы табылмады. Қайта кіріңіз.", Toast.LENGTH_LONG).show()
                            return@launch // Выходим, если пользователь не найден
                        }
                    } else { // Если бонусы не используются
                        if (user != null) {
                            user.bonusBalance += bonusEarned
                            userDao.update(user) // Вызов update на UserEntity
                            // Только если все успешно, закрываем popup
                            popup.dismiss()
                        } else {
                            // !!! ВНИМАНИЕ: Это место для потенциальных ошибок !!!
                            // Если пользователь не найден по ID, это значит, что в Room нет данных о текущем залогиненном пользователе.
                            // Вставлять "default" пользователя здесь некорректно в реальном приложении,
                            // так как пользователь уже авторизован, и его данные должны быть в Room.
                            // Вместо этого, нужно либо:
                            // 1. Убедиться, что логин-активность сохраняет UserEntity в Room.
                            // 2. Либо, если текущий пользователь не найден, запросить его данные с сервера и сохранить.
                            // 3. Либо показать ошибку и попросить пользователя перелогиниться.
                            // Для компиляции, я добавлю временные "default" значения, но это надо исправить в реальном коде.
                            userDao.insert(
                                UserEntity(
                                    id = currentLoggedInUserId, // Используем актуальный ID
                                    login = "default_login_placeholder", // Замените на реальный логин пользователя
                                    email = "default@example.com", // Замените на реальный email пользователя
                                    passwordHash = "default_hashed_password_placeholder", // Замените на хеш пароля
                                    role = "client", // Замените на актуальную роль пользователя
                                    bonusBalance = bonusEarned
                                )
                            )
                            Toast.makeText(this@CartActivity, "Пайдаланушы деректері жаңартылды (default)", Toast.LENGTH_SHORT).show()
                            popup.dismiss()
                        }
                    }

                    // Этот блок должен выполняться только после успешного обновления/вставки бонусов
                    for ((product, quantity) in cart) {
                        orderDao.insertOrder(
                            OrderEntity(
                                productTitle = product.title,
                                productPrice = product.price * quantity,
                                productImage = product.imageRes,
                                orderDate = System.currentTimeMillis()
                            )
                        )
                    }

                    CartManager.clear()
                    setupCart()
                }
            } else {
                Toast.makeText(this, "Barlyq oristerdi toltyrynyz", Toast.LENGTH_SHORT).show()
            }
        }

        popup.showAtLocation(binding.main, Gravity.CENTER, 0, 0)
    }


    private fun showWalletPopup() {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.wallet_popup, null)
        walletPopupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val radioGroup = popupView.findViewById<RadioGroup>(R.id.paymentMethodGroup)
        val cardListLayout = popupView.findViewById<LinearLayout>(R.id.cardListLayout)

        val etNumber = popupView.findViewById<EditText>(R.id.etCardNumber)
        val etExpiry = popupView.findViewById<EditText>(R.id.etExpiry)
        val etCVV = popupView.findViewById<EditText>(R.id.etCVV)
        val etHolder = popupView.findViewById<EditText>(R.id.etHolder)
        val btnSave = popupView.findViewById<Button>(R.id.btnSaveCard)

        fun toggleCardFields(show: Boolean) {
            val visibility = if (show) View.VISIBLE else View.GONE
            cardListLayout.visibility = visibility
            etNumber.visibility = visibility
            etExpiry.visibility = visibility
            etCVV.visibility = visibility
            etHolder.visibility = visibility
            btnSave.visibility = visibility
        }

        fun refreshCardList(cards: List<PaymentCard>) {
            cardListLayout.removeAllViews()
            for (card in cards) {
                val cardView = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    val text = TextView(this@CartActivity).apply {
                        text = "${card.holder} - ${card.number} (${card.expiry})"
                        textSize = 16f
                        setTextColor(Color.BLACK)
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    val delete = ImageView(this@CartActivity).apply {
                        setImageResource(android.R.drawable.ic_delete)
                        setOnClickListener {
                            lifecycleScope.launch { dao.delete(card) }
                        }
                    }
                    addView(text)
                    addView(delete)
                }
                cardListLayout.addView(cardView)
            }
        }

        // Необходимо убедиться, что LiveData наблюдается только когда Activity активна
        // Это уже делает observe, но если вы хотите управлять подпиской вручную, используйте
        // observe(this, Observer { cards -> ... })
        dao.getAll().observe(this) { cards ->
            // Проверка состояния Lifecycle, чтобы избежать проблем при обновлении UI, когда Activity не активна
            if (lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) { // Changed to STARTED for broader updates
                refreshCardList(cards)
            }
        }


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            toggleCardFields(checkedId == R.id.cardOption)
        }

        btnSave.setOnClickListener {
            val number = etNumber.text.toString().trim()
            val expiry = etExpiry.text.toString().trim()
            val cvv = etCVV.text.toString().trim()
            val holder = etHolder.text.toString().trim()

            if (number.isNotEmpty() && expiry.isNotEmpty() && cvv.isNotEmpty() && holder.isNotEmpty()) {
                val newCard = PaymentCard(number = number, expiry = expiry, cvv = cvv, holder = holder)
                lifecycleScope.launch { dao.insert(newCard) }
                etNumber.text.clear()
                etExpiry.text.clear()
                etCVV.text.clear()
                etHolder.text.clear()
            } else {
                Toast.makeText(this, "Barlyq óristerdi toltyryńyz", Toast.LENGTH_SHORT).show()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            walletPopupWindow?.showAtLocation(binding.main, Gravity.CENTER, 0, 0)
        }, 100)
    }

    override fun onDestroy() {
        walletPopupWindow?.dismiss()
        super.onDestroy()
    }
}
