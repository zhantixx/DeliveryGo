package com.example.deliverygo.Activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.deliverygo.Adapter.OnboardingItemsAdapter
import com.example.deliverygo.Domain.OnboardingItem
import com.example.deliverygo.R
import com.google.android.material.button.MaterialButton

class IntroActivity : AppCompatActivity(){
    private lateinit var  onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems(){
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.intro1,
                    title = "DeliveryGo – azyq-túlik pen tawarlardy úińizge deiin!",
                    description = "Úi’den shyqpaı-aq tapsyrys berıńiz. Biz sizge eń qajetti zat-tárdi dál waqytında jetkizemiz!"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.intro2,
                    title = "Uaqyt pen kúshdi únemdeńiz",
                    description = "DeliveryGo kómegi arqyly dúkenge barmai-aq, kúndelikti kerek zat-tardy birneshe minutta alyń."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.intro3,
                    title = "DeliveryGo – sizdiń senimdi jetkizu seriktesińiz",
                    description = "Bizben birge sizdiń satyp alýyńyz jeńil ári qauipsiz bolady. Jylдам. Ońğaıly. Senimdi."
                )
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.viewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position )
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER


        findViewById<MaterialButton>(R.id.nextBtn).setOnClickListener {
            navigateToLoginActivity()
        }
    }
    private fun navigateToLoginActivity(){
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
    private fun setupIndicators(){
        indicatorsContainer=findViewById(R.id.indicatorsContainer)
        val indicators =arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let{
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_bg
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }
    private fun setCurrentIndicator(position: Int){
        val childCount = indicatorsContainer.childCount
        for(i in 0 until childCount){
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if(i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_bg
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_bg
                    )
                )
            }
        }
    }
}