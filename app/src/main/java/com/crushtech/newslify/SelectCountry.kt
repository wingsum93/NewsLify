package com.crushtech.newslify

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.mikelau.countrypickerx.CountryPickerCallbacks
import com.mikelau.countrypickerx.CountryPickerDialog
import kotlinx.android.synthetic.main.activity_select_country.*

class SelectCountry : AppCompatActivity() {
    private var countryIsoCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_select_country)

        val btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_anim)
        btn_finish.animation = btnAnim
        btn_select_country.setOnClickListener {
            val countryPicker: CountryPickerDialog = CountryPickerDialog(
                this,
                CountryPickerCallbacks { country, _ ->
                    countryIsoCode = country.isoCode
                    country_selected.text = country.getCountryName(this)
                    country_selected.visibility = View.VISIBLE
                }, false, 0
            )
            countryPicker.show()
        }
        btn_finish.setOnClickListener {
            val country = country_selected.text
            if (country_selected.text.isNullOrEmpty()) {
                Toast.makeText(this, "please select country", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveDataToPref(country.toString())
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }
        if (savedInstanceState != null) {
            country_selected.text = savedInstanceState.getString("myCon")
            country_selected.visibility = View.VISIBLE
            countryIsoCode=savedInstanceState.getString("myConIsoCode")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("myCon", country_selected.text.toString())
        outState.putString("myConIsoCode", countryIsoCode)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString("myCon")
    }

    private fun saveDataToPref(country: String) {
        val sharedPrefs =
            applicationContext.getSharedPreferences("myprefs", Context.MODE_PRIVATE).apply {
                edit().apply {
                    putString("Country", country)
                    putString("countryIsoCode", countryIsoCode)
                    apply()
                }
            }
    }
}