package com.example.deliverygo.Activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.UserDao
import com.example.deliverygo.Data.UserEntity
import com.example.deliverygo.databinding.ActivityEditCourierProfileBinding // Убедитесь, что это ваш binding
import kotlinx.coroutines.launch

class EditCourierProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCourierProfileBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCourierProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        userDao = db.userDao()

        loadCourierProfileForEdit()
        setupClickListeners()
    }

    private fun loadCourierProfileForEdit() {
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentLoggedInUserId = sharedPrefs.getInt("logged_in_user_id", -1)

        if (currentLoggedInUserId == -1) {
            Toast.makeText(this, "Pai'dalanushy derekteri tabylmady. Qayta kiriniz.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val user = userDao.getUserById(currentLoggedInUserId)
            if (user != null) {
                binding.inputLogin.setText(user.login)
                binding.inputEmail.setText(user.email)
                binding.inputPhone.setText(user.phoneNumber)
                binding.inputVehicleType.setText(user.vehicleType)
                binding.inputLicensePlate.setText(user.licensePlate)
                binding.inputDriverLicense.setText(user.driverLicenseNumber)
            } else {
                Toast.makeText(this@EditCourierProfileActivity, "Pai'dalanushy derekteri tabylmady.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Возвращаемся на предыдущий экран
        }

        binding.btnUpdate.setOnClickListener {
            saveCourierProfile()
        }
    }

    private fun saveCourierProfile() {
        val login = binding.inputLogin.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()
        val phoneNumber = binding.inputPhone.text.toString().trim()
        val vehicleType = binding.inputVehicleType.text.toString().trim()
        val licensePlate = binding.inputLicensePlate.text.toString().trim()
        val driverLicenseNumber = binding.inputDriverLicense.text.toString().trim()

        if (login.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || vehicleType.isEmpty() ||
            licensePlate.isEmpty() || driverLicenseNumber.isEmpty()) {
            Toast.makeText(this, "Barlyq óristi toltyryńyz", Toast.LENGTH_SHORT).show()
            return
        }



        lifecycleScope.launch {
            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val currentLoggedInUserId = sharedPrefs.getInt("logged_in_user_id", -1)

            val existingUser = userDao.getUserById(currentLoggedInUserId)
            if (existingUser != null) {
                val updatedUser = existingUser.copy(
                    login = login,
                    email = email,
                    phoneNumber = phoneNumber,
                    vehicleType = vehicleType,
                    licensePlate = licensePlate,
                    driverLicenseNumber = driverLicenseNumber
                )
                userDao.update(updatedUser)
                Toast.makeText(this@EditCourierProfileActivity, "Profil sáttı jańartyldy!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@EditCourierProfileActivity, "Qate: Pai'dalanushy tabylmady.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
