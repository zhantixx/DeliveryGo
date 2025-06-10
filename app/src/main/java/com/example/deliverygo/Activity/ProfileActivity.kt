// ProfileActivity.kt
package com.example.deliverygo.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.PaymentCard
import com.example.deliverygo.Data.PaymentCardDao
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dao: PaymentCardDao
    private var walletPopupWindow: PopupWindow? = null
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("settings", MODE_PRIVATE)

        when (preferences.getString("theme", "light")) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()
        dao = db.cardDao()

        loadProfileData()

        val userDao = db.userDao()
        lifecycleScope.launch {
            var user = userDao.getUserById(1)
            if (user == null) {
                userDao.insert(
                    UserEntity(
                        id = 1,
                        login = "guest",
                        passwordHash = "guest123",
                        email = "example@example.com",
                        role = "client",
                        bonusBalance = 0
                    )
                )

                user = userDao.getUserById(1)
            }
            binding.profileBonus.text = "${user?.bonusBalance ?: 0} ₸"
        }

        binding.exitImg.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        }

        binding.kelImg.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({ showWalletPopup() }, 100)
        }

        binding.qupImg.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, PrivacyActivity::class.java))
        }

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
                R.id.navigation_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.navigation_story -> {
                    startActivity(Intent(this, StoryActivity::class.java))
                    true
                }
                R.id.navigation_profile -> true
                else -> false
            }
        }

        binding.profBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ProfileEditActivity::class.java))
        }
        binding.darkMode.setOnClickListener {
            val editor = preferences.edit()
            val currentTheme = preferences.getString("theme", "light")

            if (currentTheme == "light") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putString("theme", "dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putString("theme", "light")
            }

            editor.apply()
            val intent = intent
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("user_profile", MODE_PRIVATE)

        binding.profileName.text = prefs.getString("name", "Example")
        binding.profileEmail.text = prefs.getString("email", "example@example.com")
        binding.profileAddress.text = prefs.getString("address", "example")
        binding.profileLogin.text = prefs.getString("name", "Example")
        binding.profilePhone.text = prefs.getString("phone", "+7 *** *** ** **")

        val imageUriString = prefs.getString("image_uri", null)
        if (imageUriString != null) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                if (inputStream != null) {
                    binding.imageView4.setImageURI(imageUri)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
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
                    val text = TextView(this@ProfileActivity).apply {
                        text = "${card.holder} - ${card.number} (${card.expiry})"
                        textSize = 16f
                        setTextColor(Color.BLACK)
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    val delete = ImageView(this@ProfileActivity).apply {
                        setImageResource(android.R.drawable.ic_delete)
                        setOnClickListener {
                            lifecycleScope.launch {
                                dao.delete(card)
                            }
                        }
                    }
                    addView(text)
                    addView(delete)
                }
                cardListLayout.addView(cardView)
            }
        }

        dao.getAll().observe(this) { cards ->
            if (lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
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
                lifecycleScope.launch {
                    dao.insert(newCard)
                }
                etNumber.text.clear()
                etExpiry.text.clear()
                etCVV.text.clear()
                etHolder.text.clear()
            } else {
                Toast.makeText(this, "Barlıq óristerdi toltyryńyz", Toast.LENGTH_SHORT).show()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            walletPopupWindow?.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0)
        }, 100)
    }

    override fun onDestroy() {
        walletPopupWindow?.dismiss()
        super.onDestroy()
    }
}
