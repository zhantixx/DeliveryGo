package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliverygo.Adapter.LikeAdapter
import com.example.deliverygo.Data.LikeManager
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityLikeBinding

class LikeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikeBinding
    private lateinit var adapter: LikeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val favList = LikeManager.getFavorites().toMutableList()

        adapter = LikeAdapter(favList,
            onDeleteClick = { product ->
                LikeManager.removeFavorite(product)
                favList.remove(product)
                adapter.notifyDataSetChanged()
            },
            onItemClick = { product ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )

        binding.likeRecycler.layoutManager = LinearLayoutManager(this)
        binding.likeRecycler.adapter = adapter



        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_like -> true
                R.id.navigation_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.navigation_story, R.id.navigation_story -> {
                    startActivity(Intent(this, StoryActivity::class.java))
                    true
                }
                R.id.navigation_profile, R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
