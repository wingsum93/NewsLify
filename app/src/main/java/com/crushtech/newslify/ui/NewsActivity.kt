package com.crushtech.newslify.ui
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crushtech.newslify.KeepStateNavigator
import com.crushtech.newslify.R
import com.crushtech.newslify.db.ArticleDatabase
import com.crushtech.newslify.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel

    @SuppressLint("RestrictedApi")
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
// get fragment
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)!!
//        val navController=findNavController(R.id.newsNavHostFragment)
//        // setup custom navigator
//        val navigator = KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.newsNavHostFragment)
//        navController.navigatorProvider += navigator
//
//        navController.setGraph(R.navigation.news_nav_graph)
//        bottomNavigationView.setupWithNavController(navController)
    }

    fun hideBottomNavigation() {
        try {
            bottomNavigationView.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    fun showBottomNavigation() {
        try {
            bottomNavigationView.visibility = View.VISIBLE
        } catch (e: Exception) {
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = newsNavHostFragment.findNavController()
        return navController.navigateUp()
    }

}