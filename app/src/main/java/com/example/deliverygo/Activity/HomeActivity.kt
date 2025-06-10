package com.example.deliverygo.Activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliverygo.Adapter.CategoryAdapter
import com.example.deliverygo.Adapter.ProductAdapter
import com.example.deliverygo.Domain.CategoryModel
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.R
import com.example.deliverygo.ViewModel.MainViewModel
import com.example.deliverygo.databinding.ActivityHomeBinding
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: MainViewModel by viewModels()
    private var fullProductList: List<ProductModel> = emptyList()
    private lateinit var productAdapter: ProductAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCategory()
        initProducts()
        initTopProducts()
        initSearch()


        binding.notImg.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_like -> {
                    startActivity(Intent(this, LikeActivity::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.navigation_story-> {
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

    private fun initCategory() {
        binding.progressBarCat.visibility = View.VISIBLE
        viewModel.categories.observe(this) { categoryList ->
            binding.progressBarCat.visibility = View.GONE
            val adapter = CategoryAdapter(ArrayList(categoryList))
            binding.catView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.catView.adapter = adapter

            adapter.setOnItemClickListener { category: CategoryModel ->
                viewModel.selectCategory(category.id)
            }
        }
    }

    private fun initProducts() {
        binding.progressBarCatDet.visibility = View.VISIBLE
        viewModel.products.observe(this) { products ->
            binding.progressBarCatDet.visibility = View.GONE
            productAdapter = ProductAdapter(products.toMutableList()) { selectedProduct ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", selectedProduct)
                startActivity(intent)
            }
            binding.catDetView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.catDetView.adapter = productAdapter

        }
    }


    private fun initTopProducts() {
        binding.progressBarTop.visibility = View.VISIBLE
        viewModel.topProducts.observe(this) { products ->
            binding.progressBarTop.visibility = View.GONE
            val adapter = ProductAdapter(products.toMutableList()) { selectedProduct ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", selectedProduct)
                startActivity(intent)
            }
            binding.topView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.topView.adapter = adapter
        }
    }

    private fun initSearch() {
            binding.searchTxt.setOnEditorActionListener { v, actionId, event ->
                val query = binding.searchTxt.text.toString().trim()
                filterProducts(query)
                true
            }



        binding.searchTxt.setOnTouchListener { v, event ->
            val drawableEnd = 2
            val drawableWidth = binding.searchTxt.compoundDrawables[drawableEnd]?.bounds?.width() ?: 0
            if (event.x >= (binding.searchTxt.width - binding.searchTxt.paddingEnd - drawableWidth)) {
                startVoiceRecognition()
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun filterProducts(query: String) {
        val filteredList = viewModel.products.value?.filter {
            it.title.contains(query, ignoreCase = true)
        } ?: emptyList()

        productAdapter.updateList(filteredList)
    }


    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Aitysynyz")
        }
        try {
            startActivityForResult(intent, 101)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                binding.searchTxt.setText(result[0])
                filterProducts(result[0])
            }
        }
    }
}


