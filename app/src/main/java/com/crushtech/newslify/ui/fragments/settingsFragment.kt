package com.crushtech.newslify.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        change_country.text = "Change Country: ${country?.capitalize()}"
        setupDailyGoals()
        setupCountry()
        setupPrivacyPolicy()
        setUpShareFunction()
        setUpRateApp()
    }

    private fun setupDailyGoals() {
        setnewsGoals.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.news_goals_dialog)
            val dismissDialog = dialog.findViewById<Button>(R.id.btn_cancel)
            val setDailyCount = dialog.findViewById<Button>(R.id.btn_set)
            val goalCount = dialog.findViewById<EditText>(R.id.goal_count)
            val Anim: Animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.fade_in
            )
            val prefs = requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
            val getPrefsCount = prefs.getInt("Goal Count", 5)
            goalCount.setText(getPrefsCount.toString())

            dismissDialog.setOnClickListener {
                dismissDialog.animation = Anim
                dialog.dismiss()
            }
            setDailyCount.setOnClickListener {
                setDailyCount.animation = Anim
                goalCount.let {
                    var goalCountText = it.text.toString()
                    if (TextUtils.isEmpty(goalCountText) || goalCountText == "0") {
                        goalCountText = "5"
                    }
                    requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
                        .edit().putInt("Goal Count", goalCountText.toInt()).apply()
                }
                view?.let { it1 ->
                    SimpleCustomSnackbar.make(
                        it1, "Your Goal is set: you're good to go",
                        Snackbar.LENGTH_LONG, null, R.drawable.rocket, "",
                        null
                    )?.show()
                }
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                hideKeyboard()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.setCancelable(false)
        }
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
                        null
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}