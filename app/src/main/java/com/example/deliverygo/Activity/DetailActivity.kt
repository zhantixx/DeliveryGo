package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.deliverygo.Activity.PrivacyActivity
import com.example.deliverygo.Data.CartManager
import com.example.deliverygo.Data.LikeManager
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var product: ProductModel
    private lateinit var currentProduct: ProductModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentProduct = intent.getParcelableExtra("product")!!

        binding.titleTxt.text = currentProduct.title
        binding.descTxt.text = currentProduct.description
        binding.priceTxt.text = "${currentProduct.price} tg"
        binding.addressTxt.text = "Reiting: ${currentProduct.rating}" // при наличии

        binding.favBtn.setOnClickListener {
            LikeManager.addFavorite(currentProduct)
            Toast.makeText(this, "Önım tañdaulylaryñyzğa qosyldy.!", Toast.LENGTH_SHORT).show()
        }

        binding.shareBtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tauarmen bolisu")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Tauar: ${currentProduct.title} - ${currentProduct.price} tg")
            startActivity(Intent.createChooser(shareIntent, "Bolisu tasilin tandanyz"))
        }


        product = intent.getParcelableExtra("product")!!

        binding.apply {
            titleTxt.text = product.title
            descTxt.text = product.description
            addressTxt.text = "⭐ ${product.rating}"
            priceTxt.text = "${product.price.toInt()} tg"
            Glide.with(this@DetailActivity).load(product.imageRes).into(imageView10)

            backBtn.setOnClickListener {
                startActivity(Intent(this@DetailActivity, HomeActivity::class.java))
            }

            sebetBtn.setOnClickListener {
                CartManager.addToCart(product)
                Toast.makeText(this@DetailActivity, "Sebetke qosyldy", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
