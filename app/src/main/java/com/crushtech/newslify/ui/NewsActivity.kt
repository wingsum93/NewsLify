package com.crushtech.newslify.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crushtech.newslify.R
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.repository.NewsRepository
import com.crushtech.newslify.ui.util.Constants.Companion.CACHE_SIZE
import kotlinx.android.synthetic.main.activity_news.*
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        //  getCountryDataFromPrefs()
        supportActionBar?.show()
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository, application)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

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
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager = applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = newsNavHostFragment.findNavController()
        return navController.navigateUp()
    }

}