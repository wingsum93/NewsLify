package com.crushtech.newslify

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crushtech.newslify.ui.NewsViewModelProviderFactory
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.repository.NewsRepository
import com.crushtech.newslify.ui.NewsViewModel
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        getCountryDataFromPrefs()
        supportActionBar?.show()
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository,application)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        val appBarConfig= AppBarConfiguration(setOf(R.id.breakingNewsFragment,
            R.id.savedNewsFragment,
            R.id.searchNewsFragment)
        )

        setupActionBarWithNavController(newsNavHostFragment.findNavController(),appBarConfig)
    }

    private fun getCountryDataFromPrefs(){
        val prefs=applicationContext.getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val countryIsoCode = prefs.getString("countryIsoCode","US")
       // mainText.text=countryIsoCode
    }

    fun hideBottomNavigation(){
        bottomNavigationView.visibility= View.GONE
    }
    fun showBottomNavigation(){
        bottomNavigationView.visibility= View.VISIBLE
    }
}