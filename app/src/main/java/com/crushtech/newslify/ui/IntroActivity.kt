package com.crushtech.newslify.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.viewpager.widget.ViewPager
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.IntroViewPagerAdapter
import com.crushtech.newslify.models.ScreenItems
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.ArrayList

class IntroActivity : AppCompatActivity() {
    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private var mList: ArrayList<ScreenItems>? = null
    private lateinit var tabIndicator: TabLayout
    private var position: Int = 0
    lateinit var btnAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //check if this activity has been launched earlier than this
//        if(restoreInstancePrefs()){
//            val intent = Intent(this, NewsActivity::class.java)
//            startActivity(intent)
//             finish()
//        }
        setContentView(R.layout.activity_intro)


        //initialize views
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(this,
            R.anim.button_anim
        )

        //setUp screen
        setUpListScreens()

        //setup viewpager
        screenPager = findViewById(R.id.viewScreenPager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList!!)
        screenPager.adapter = introViewPagerAdapter

        //setup tablayout with slider viewpager
        tabIndicator.setupWithViewPager(screenPager)

        btn_next.setOnClickListener {
            position = screenPager.currentItem
            if (position < mList!!.size) {
                position++
                screenPager.currentItem = position
            }
            if (position == mList!!.size - 1) {
                loadLastScreen()
            }else{
                resetToNormalScreen()
            }

        }
        tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == mList!!.size - 1) {
                    loadLastScreen()
                }
                else{
                    resetToNormalScreen()
                }
            }

        })

        btn_get_started.setOnClickListener {
            val intent = Intent(this, SelectCountry::class.java)
            startActivity(intent)
            saveInstanceToPrefs()
            finish()
        }
    }

    private fun setUpListScreens() {
        mList = ArrayList()
        mList!!.add(
            ScreenItems(
                "Welcome to Newslify",
                "stay up-to-date with newslify news app, get insights on businesses, trending,breaking news and so much more",
                R.raw.news2
            )
        )
        mList!!.add(
            ScreenItems(
                "Trending News",
                "get trending news from any part of the globe",
                R.raw.trendingnews
            )
        )
        mList!!.add(
            ScreenItems(
                "Search for news you desire",
                "easily search for news topics you desire for e.g sports,bitcoin etc",
                R.raw.searchnews
            )
        )
        mList!!.add(
            ScreenItems(
                "Never miss out",
                "favorite news you love for easy access later",
                R.raw.favorite
            )
        )
        mList!!.add(
            ScreenItems(
                "Push Notification",
                "get notified once there's a breaking news/article",
                R.raw.notification
            )
        )
        mList!!.add(
            ScreenItems(
                "All Set",
                "to enjoy these features, please click on get started",
                R.raw.checked_done
            )
        )
    }

    private fun loadLastScreen() {
        btn_next.visibility = View.INVISIBLE
        btn_get_started.visibility = View.VISIBLE
        tabIndicator.visibility = View.INVISIBLE
        btn_get_started.animation = btnAnim
    }
    private fun resetToNormalScreen(){
        btn_next.visibility = View.VISIBLE
        btn_get_started.visibility = View.INVISIBLE
        tabIndicator.visibility = View.VISIBLE
    }
    private fun saveInstanceToPrefs(){
        val sharedPrefs=applicationContext.getSharedPreferences(
            "prefs", Context.MODE_PRIVATE).apply {
            edit().putBoolean("first time user",true).apply()
        }
    }
    private fun restoreInstancePrefs():Boolean{
        val prefs=applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("first time user",false)
    }
}