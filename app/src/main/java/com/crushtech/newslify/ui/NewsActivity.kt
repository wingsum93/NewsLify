package com.crushtech.newslify.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.crushtech.newslify.R
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.repository.NewsRepository
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_news.*


class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var newsRepository: NewsRepository

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpThemes()
        setContentView(R.layout.activity_news)


        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        supportActionBar?.show()
        newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository, application)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navController = Navigation.findNavController(this, R.id.newsNavHostFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (it.itemId != bottomNavigationView.selectedItemId) {
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }
        bottomNavigationView.setOnNavigationItemReselectedListener {}
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        adView.adListener = object : AdListener() {
        }

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
            adView.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    fun showBottomNavigation() {
        try {
            bottomNavigationView.visibility = View.VISIBLE
            adView.visibility = View.VISIBLE
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
        when (sharedprefs.getInt("switchPos", 5)) {
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
            4 -> {
                setTheme(R.style.PremiumThemeFive)
            }
            5 -> {
                setTheme(R.style.DefaultTheme)
            }
        }
    }

    object GetCurrentTheme {
        //get current theme
        fun currentTheme(activity: Activity): Int {
            val componentName = activity.componentName
            return activity.packageManager.getActivityInfo(
                componentName,
                0
            ).themeResource
        }


    }
}