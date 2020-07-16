package com.crushtech.newslify.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.crushtech.newslify.R
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.PRIVACY_POLICY
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.google.android.material.snackbar.Snackbar
import com.mikelau.countrypickerx.CountryPickerCallbacks
import com.mikelau.countrypickerx.CountryPickerDialog
import kotlinx.android.synthetic.main.settings_layout.*

class settingsFragment : Fragment(R.layout.settings_layout) {
    private var countryPicker: CountryPickerDialog? = null
    private var countryIsoCode: String? = null
    private var myCountry: String? = null
    private var textAnim: Animation? = null
    private lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
        val streakCount = prefs.getInt(STREAK, 0)
        dailynewsGoals.text = "Goals reached: $streakCount news articles read today"

        textAnim = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )


        val conPrefs = requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val country = conPrefs.getString("Country", "")
        change_country.text = "Change Country: ${country?.toLowerCase()}"

        setupCountry()
        setupPrivacyPolicy()
        setUpShareFunction()
        setUpRateApp()
    }

    private fun setupCountry() {
        change_country.setOnClickListener {
            change_country.animation = textAnim
            countryPicker = CountryPickerDialog(
                requireContext(),
                CountryPickerCallbacks { country, _ ->
                    countryIsoCode = country.isoCode
                    myCountry = country.getCountryName(context)
                    change_country.text = "Change Country: ${myCountry?.toLowerCase()}"
                    myCountry?.let { saveDataToPref(it) }
                    SimpleCustomSnackbar.make(
                        settings_coordinator, "Country changed: please RESTART the app",
                        Snackbar.LENGTH_INDEFINITE, null,
                        R.drawable.country_changed_icon, "",
                        ContextCompat.getColor(requireContext(), R.color.mygrey)
                    )?.show()
                }, false, 0
            )
            countryPicker!!.show()
        }

    }

    private fun saveDataToPref(country: String) {
        requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE).apply {
            edit().apply {
                putString("Country", country)
                putString("countryIsoCode", countryIsoCode)
                apply()
            }
        }
    }

    private fun setupPrivacyPolicy() {
        privacy_policy.setOnClickListener {
            privacy_policy.animation = textAnim
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(PRIVACY_POLICY)
            startActivity(intent)
        }

    }

    private fun setUpRateApp() {
        rate_app.setOnClickListener {
            rate_app.animation = textAnim
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + requireContext().packageName)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                    )
                )
            }

        }
    }

    private fun setUpShareFunction() {
        shareApp.setOnClickListener {
            shareApp.animation = textAnim
            val shareIntent = Intent(Intent.ACTION_SEND)
            val appPackageName =
                requireContext().applicationContext.packageName
            val strAppLink: String
            strAppLink = try {
                "https://play.google.com/store/apps/details?id$appPackageName"
            } catch (anfe: ActivityNotFoundException) {
                "https://play.google.com/store/apps/details?id$appPackageName"
            }
            shareIntent.type = "text/link"
            val shareBody =
                "Hey, Check out NewsLify, i use it to read trending news of all kinds . Get it for free at \n$strAppLink"
            val shareSub = "APP NAME/TITLE"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(shareIntent, "Share Using"))
        }

    }

    override fun onStop() {
        countryPicker?.dismiss()
        super.onStop()
    }
}