package com.crushtech.newslify.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.PremiumThemesAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.fragments.settingsFragment.ShowUpgradePopUpDialog.showPopupDialog
import com.crushtech.newslify.ui.util.Constants.Companion.PRIVACY_POLICY
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.mikelau.countrypickerx.CountryPickerCallbacks
import com.mikelau.countrypickerx.CountryPickerDialog
import kotlinx.android.synthetic.main.settings_layout.*
import org.w3c.dom.Text
import java.util.ArrayList

data class ThemeItems(val themeName: String, val lottieRaw: Int, var switchState: Boolean)

class settingsFragment : Fragment() {
    private var countryPicker: CountryPickerDialog? = null
    private var countryIsoCode: String? = null
    private var myCountry: String? = null
    private var textAnim: Animation? = null
    private var isPremiumUser = false
    private lateinit var setnewsGoals: TextView
    private lateinit var change_country: TextView
    private lateinit var privacy_policy: TextView
    private lateinit var rate_app: TextView
    private lateinit var shareApp: TextView
    private lateinit var whyUgrade: MaterialButton
    private lateinit var purchaseThemes: TextView
    private lateinit var viewModel: NewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val myView = inflater.inflate(R.layout.settings_layout, container, false)

        setnewsGoals = myView.findViewById(R.id.setnewsGoals)
        change_country = myView.findViewById(R.id.change_country)
        privacy_policy = myView.findViewById(R.id.privacy_policy)
        rate_app = myView.findViewById(R.id.rate_app)
        shareApp = myView.findViewById(R.id.shareApp)
        whyUgrade = myView.findViewById(R.id.whyUpgrade)
        purchaseThemes = myView.findViewById(R.id.purchaseThemes)

        viewModel = (activity as NewsActivity).newsViewModel
        try {
            val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
            val streakCount = prefs.getInt(STREAK, 0)
            myView.findViewById<TextView>(R.id.dailynewsGoals).text =
                "Goals reached: $streakCount news articles read today"
        } catch (e: Exception) {
        }

        textAnim = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )

        val conPrefs = requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val country = conPrefs.getString("Country", "")
        myView.findViewById<TextView>(R.id.change_country).text =
            "Change Country: ${country?.capitalize()}"
        setupDailyGoals()
        setupCountry()
        setupPrivacyPolicy()
        setUpShareFunction()
        setUpRateApp()
        setupWhyUpgradePopUp()
        setupPurchaseThemes()
        return myView
    }

    private fun setupDailyGoals() {
        setnewsGoals.setOnClickListener {
            if (!isPremiumUser) {
                showPopupDialog(requireContext())
            } else {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.news_goals_dialog)
                val dismissDialog = dialog.findViewById<Button>(R.id.btn_cancel)
                val setDailyCount = dialog.findViewById<Button>(R.id.btn_set)
                val goalCount = dialog.findViewById<EditText>(R.id.goal_count)
                val Anim: Animation = AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_in
                )
                val prefs =
                    requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
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
    }

    private fun setupCountry() {
        change_country.setOnClickListener {
            change_country.animation = textAnim
            countryPicker = CountryPickerDialog(
                requireContext(),
                CountryPickerCallbacks { country, _ ->
                    countryIsoCode = country.isoCode
                    myCountry = country.getCountryName(context)
                    change_country.text = "Change Country: ${myCountry?.capitalize()}"
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

    private fun setupWhyUpgradePopUp() {
        whyUgrade.setOnClickListener {
            showPopupDialog(requireContext())
        }
    }

    object ShowUpgradePopUpDialog {
        fun showPopupDialog(context: Context) {
            val animation = AnimationUtils.loadAnimation(
                context,
                R.anim.button_anim
            )
            val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.upgrade_popup_dialog)
            val dismissDialog = dialog.findViewById<ImageView>(R.id.close_popup)
            val upgradeTxt = dialog.findViewById<TextView>(R.id.popup_ugrade_txt)
            upgradeTxt.animation = animation
            dismissDialog.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    private fun setupPurchaseThemes() {
        purchaseThemes.setOnClickListener {
            if (!isPremiumUser) {
                showPopupDialog(requireContext())
            } else {
                val dialog =
                    Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                dialog.setContentView(R.layout.customthemes_popup_dialog)
                val dismissDialog = dialog.findViewById<ImageView>(R.id.theme_close_popup)
                val recyclerView = dialog.findViewById<RecyclerView>(R.id.customThemesRv)
                val themesItems: ArrayList<ThemeItems>? = ArrayList()
                val themesAdapter = PremiumThemesAdapter()
                themesItems!!.add(ThemeItems("Premium Theme 1", R.raw.themes, false))
                themesItems.add(ThemeItems("Premium Theme 2", R.raw.themes, false))
                themesItems.add(ThemeItems("Premium Theme 3", R.raw.themes, false))
                themesItems.add(ThemeItems("Premium Theme 4", R.raw.themes, false))
                themesItems.add(ThemeItems("Default AppTheme", R.raw.themes, false))

                themesAdapter.differ.submitList(themesItems)
                recyclerView.apply {
                    adapter = themesAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }

                dismissDialog.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.setCancelable(false)
            }
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