package com.crushtech.newslify.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crushtech.newslify.R
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.repository.NewsRepository
import com.crushtech.newslify.ui.fragments.settingsFragment
import com.google.android.gms.ads.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_news.*
import com.crushtech.newslify.ui.fragments.settingsFragment.ShowUpgradePopUpDialog

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpThemes()
        setContentView(R.layout.activity_news)

        MobileAds.initialize(this) {}
        val testDeviceIds = listOf("3001A61A70E03E82A3CD3B4A7DB8A906", AdRequest.DEVICE_ID_EMULATOR)
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        //show upgrade dialog
        close_ad.setOnClickListener {
            ShowUpgradePopUpDialog.showPopupDialog(this, this)
        }

        supportActionBar?.show()
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository, application)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navController = Navigation.findNavController(this, R.id.newsNavHostFragment)
        // bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (it.itemId != bottomNavigationView.selectedItemId) {
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }
        bottomNavigationView.setOnNavigationItemReselectedListener {}
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

//        adView.adListener = object :AdListener(){
//            override fun onAdLoaded() {
//                adParent.visibility = View.VISIBLE
//                close_ad.setOnClickListener {
//                    adParent.visibility = View.GONE
//                }
//                super.onAdLoaded()
//            }
//        }

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.breakingNewsFragment,
                R.id.exploreFragment,
                R.id.savedNewsFragment,
                R.id.searchNewsFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(newsNavHostFragment.findNavController(), appBarConfig)

    }

    fun hideBottomNavigation() {
        try {
            bottomNavigationView.visibility = View.GONE
            adParent.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    fun showBottomNavigation() {
        try {
            bottomNavigationView.visibility = View.VISIBLE
            adParent.visibility = View.VISIBLE
        } catch (e: Exception) {
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = newsNavHostFragment.findNavController()
        return navController.navigateUp()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        adView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun setUpThemes() {
        val sharedprefs: SharedPreferences =
            this.getSharedPreferences("switchState", Context.MODE_PRIVATE)
        when (sharedprefs.getInt("switchPos", 4)) {
            0 -> {
                setTheme(R.style.PremiumThemeOne)
            }
            1 -> {
                setTheme(R.style.PremiumThemeTwo)
            }
            2 -> {
                setTheme(R.style.PremiumThemeThree)
            }
            3 -> {
                setTheme(R.style.PremiumThemeFour)
            }
            else -> {
                setTheme(R.style.DefaultTheme)
            }
        }
    }
}