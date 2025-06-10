package com.example.deliverygo.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliverygo.Adapter.CourierOrderHistoryAdapter
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Data.CourierOrderDao
import com.example.deliverygo.databinding.ActivityCourierOrderHistoryBinding // Новый binding
import kotlinx.coroutines.launch

class CourierOrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourierOrderHistoryBinding
    private lateinit var courierOrderDao: CourierOrderDao
    private lateinit var historyAdapter: CourierOrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourierOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        courierOrderDao = db.courierOrderDao()

        setupHistoryRecyclerView()
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = CourierOrderHistoryAdapter(mutableListOf())
        binding.historyOrdersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CourierOrderHistoryActivity)
            adapter = historyAdapter
        }

        courierOrderDao.getOrderHistory().observe(this) { orders ->
            historyAdapter.updateOrders(orders)
            if (orders.isEmpty()) {
                binding.noHistoryMessage.visibility = View.VISIBLE
                binding.historyOrdersRecyclerView.visibility = View.GONE
            } else {
                binding.noHistoryMessage.visibility = View.GONE
                binding.historyOrdersRecyclerView.visibility = View.VISIBLE
            }
        }
    }
}
