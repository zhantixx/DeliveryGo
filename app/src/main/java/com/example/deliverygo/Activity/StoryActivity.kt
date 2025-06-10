package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.deliverygo.Adapter.OrderAdapter
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityStoryBinding
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home ->{
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
                R.id.navigation_story-> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()

        adapter = OrderAdapter { order ->
            lifecycleScope.launch {
                db.orderDao().deleteOrder(order)
                Toast.makeText(this@StoryActivity, "Tauar qaitaryldy", Toast.LENGTH_SHORT).show()
            }
        }

        binding.orderRecycler.layoutManager = LinearLayoutManager(this)
        binding.orderRecycler.adapter = adapter

        db.orderDao().getAllOrders().observe(this) { orders ->
            adapter.submitList(orders)
        }
    }
}
