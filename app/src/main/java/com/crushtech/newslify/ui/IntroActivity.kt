package com.crushtech.newslify.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.IntroViewPagerAdapter
import com.crushtech.newslify.models.ScreenItems
import com.crushtech.newslify.ui.util.Constants.Companion.PRIVACY_POLICY
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
        if (restoreInstancePrefs()) {
            val intent = Intent(this, WelcomeScreen::class.java)
            startActivity(intent)
            finish()
        }
        //restore selectCountry activity
        if (restoreInstancePrefs() && selectCountryWasKilled()) {
            val intent = Intent(this, SelectCountry::class.java)
            startActivity(intent)
        }

        setContentView(R.layout.activity_intro)


        //initialize views
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(
            this,
            R.anim.button_anim
        )

        //setUp screen
        setUpScreenLists()

        //setup viewpager
        screenPager = findViewById(R.id.viewScreenPager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList!!)
        screenPager.adapter = introViewPagerAdapter

        //setup tablayout with slider viewpager
        tabIndicator.setupWithViewPager(screenPager)


        if (position == 0) {
            tabIndicator.visibility = View.INVISIBLE
            term_of_service.makeLinks(Pair("Term of service", View.OnClickListener {
                showBrowser(PRIVACY_POLICY)
            }), Pair("Privacy policy", View.OnClickListener {
                showBrowser(PRIVACY_POLICY)
            }))
        }

        btn_next.setOnClickListener {
            position = screenPager.currentItem
            when {
                position < mList!!.size -> {
                    position++
                    screenPager.currentItem = position
                }

                position == mList!!.size - 1 -> {
                    loadLastScreen()
                }
                else -> {
                    resetToNormalScreen()
                }
            }

        }
        tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                term_of_service.visibility = View.INVISIBLE
                when {
                    tab!!.position == mList!!.size - 1 -> {
                        loadLastScreen()
                    }
                    tab.position == 0 -> {
                        tabIndicator.visibility = View.INVISIBLE
                        term_of_service.visibility = View.VISIBLE
                    }
                    else -> {
                        resetToNormalScreen()
                    }
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

    private fun setUpScreenLists() {
        mList = ArrayList()
        mList!!.add(
            ScreenItems(
                "Welcome to Newslify",
                " ",
                R.raw.bg
            )
        )
        mList!!.add(
            ScreenItems(
                "Stay Updated",
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
                "save news articles you love for easy access later",
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

    private fun resetToNormalScreen() {
        btn_next.visibility = View.VISIBLE
        btn_get_started.visibility = View.INVISIBLE
        tabIndicator.visibility = View.VISIBLE
    }

    private fun saveInstanceToPrefs() {
        val sharedPrefs = applicationContext.getSharedPreferences(
            "prefs", Context.MODE_PRIVATE
        ).apply {
            edit().putBoolean("first time user", true).apply()
        }
    }

    private fun restoreInstancePrefs(): Boolean {
        val prefs = applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("first time user", false)
    }

    //for onboarding term of service and privacy policy
    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    Selection.setSelection((p0 as TextView).text as Spannable, 0)
                    p0.invalidate()
                    link.second.onClick(p0)
                }

            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink,
                startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod = LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    //browser function
    private fun showBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun selectCountryWasKilled(): Boolean {
        val selectCountryPrefs =
            applicationContext.getSharedPreferences("selectCountry", Context.MODE_PRIVATE)
        return selectCountryPrefs.getBoolean("isStopped", false)
    }
}