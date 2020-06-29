package com.crushtech.newslify

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        getCountryDataFromPrefs()
    }

    private fun getCountryDataFromPrefs(){
        val prefs=applicationContext.getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val countryIsoCode = prefs.getString("countryIsoCode","US")
        mainText.text=countryIsoCode
    }
}