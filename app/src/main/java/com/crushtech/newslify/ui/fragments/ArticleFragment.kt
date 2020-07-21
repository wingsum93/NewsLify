package com.crushtech.newslify.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.R
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.google.android.gms.ads.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ArticleFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var isClicked = false
    private var btnCount = 0
    private var streakCount = 0
    private lateinit var mInterstitialAd: InterstitialAd

    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as NewsActivity).window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val view = layoutInflater.inflate(R.layout.fragment_article, container, false)
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).supportActionBar?.hide()
        (activity as NewsActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        retainInstance = true
        //get the argument from the generated arg class
        val article = args.article
        val viewPos: View = view.findViewById(R.id.myCord)
        val logoAnim: Animation = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )
        MobileAds.initialize(context) {}
        val testDeviceIds = listOf("3001A61A70E03E82A3CD3B4A7DB8A906", AdRequest.DEVICE_ID_EMULATOR)
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = "ca-app-pub-7292512767354152/3859686593"
        val adRequest1: AdRequest = AdRequest.Builder().build()
        mInterstitialAd.loadAd(adRequest1)


        view.findViewById<FloatingActionButton>(R.id.fab_favorite).setOnClickListener {
            val customSnackListener: View.OnClickListener = View.OnClickListener {
                findNavController().navigate(R.id.action_articleFragment_to_savedNewsFragment)
            }
            isClicked = true
            if (isClicked && btnCount == 0) {
                val date = Calendar.getInstance().time
                val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
                val strDate = dateFormat.format(date)
                article.timeInsertedToRoomDatabase = strDate
                viewModel.saveArticle(article)

                view.findViewById<FloatingActionButton>(R.id.fab_favorite).apply {
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                    startAnimation(logoAnim)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        lottie_saved_anim.visibility = View.VISIBLE
                        lottie_webview_loading.visibility = View.INVISIBLE
                        webview_loading_text1.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                    }
                    delay(3000L)
                    try {
                        lottie_saved_anim.visibility = View.GONE
                        lottie_webview_loading.visibility = View.VISIBLE
                        webview_loading_text1.visibility = View.VISIBLE
                    } catch (e: Exception) {
                    }
                }

                SimpleCustomSnackbar.make(
                    viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", null
                )?.show()
                val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
                streakCount = prefs.getInt(STREAK, 0)
                streakCount++
                requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
                    .edit().putInt(STREAK, streakCount).apply()
                val cal = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
                requireContext().getSharedPreferences("TIME", Context.MODE_PRIVATE).edit()
                    .putInt("TIME", cal).apply()
            } else {
                lottie_saved_anim.visibility = View.GONE
                SimpleCustomSnackbar.make(
                    viewPos, "Article already saved", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", null
                )?.show()
            }
            btnCount++

        }


        //streak logic
        viewModel.getSavedNews().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            GlobalScope.launch(Dispatchers.Main) {
                delay(200L)
                val sharedprefs =
                    requireContext().getSharedPreferences("TIME", Context.MODE_PRIVATE)
                val previousDay = sharedprefs.getInt("TIME", 0)

                val usersGoalPrefs =
                    requireContext().getSharedPreferences("Goal Count", Context.MODE_PRIVATE)
                val getUsersGoalSet = usersGoalPrefs.getInt("Goal Count", 5)
                val currentDay = Calendar.getInstance()[Calendar.DAY_OF_YEAR]

                if (currentDay > previousDay) {
                    context?.getSharedPreferences(STREAK, Context.MODE_PRIVATE)?.edit()?.clear()
                        ?.apply()
                    requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
                        .edit().putInt(STREAK, streakCount).apply()
                    context?.getSharedPreferences("TIME", Context.MODE_PRIVATE)?.edit()?.clear()
                        ?.apply()
                    requireContext().getSharedPreferences("TIME", Context.MODE_PRIVATE)
                        .edit().putInt("TIME", currentDay).apply()
                    //   resetStreakAtMidnight(currentDay)
                }

                if (isClicked && streakCount == getUsersGoalSet) {
                    SimpleCustomSnackbar.make(
                        viewPos, "Daily news article goal reached :) ", Snackbar.LENGTH_LONG,
                        null, R.drawable.streak_icon,
                        "", null
                    )?.show()
                    mInterstitialAd.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            mInterstitialAd.show()
                            super.onAdLoaded()
                        }
                    }

                } else if (isClicked && streakCount > getUsersGoalSet && streakCount % getUsersGoalSet == 0) {
                    SimpleCustomSnackbar.make(
                        viewPos,
                        "x${streakCount / getUsersGoalSet} of daily goals reached: you're on fire ",
                        Snackbar.LENGTH_LONG,
                        null,
                        R.drawable.rocket,
                        "",
                        null
                    )?.show()
                    mInterstitialAd.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            mInterstitialAd.show()
                            super.onAdLoaded()
                        }
                    }
                }

            }
        })


        view.findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            val articleUrl = "From NewsLify:  ${article.url}"
            val shareSub = "APP NAME/TITLE"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/link"
                putExtra(Intent.EXTRA_SUBJECT, shareSub)
                putExtra(Intent.EXTRA_TEXT, articleUrl)
            }
            startActivity(Intent.createChooser(intent, "Share Using"))
        }

        val bottomSheet: NestedScrollView = view.findViewById(R.id.my_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<WebView>(R.id.scrollwebView).apply {
            this.settings.cacheMode = WebSettings.LOAD_DEFAULT
            this.settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        lottie_webview_loading.visibility = View.INVISIBLE
                        webview_loading_text1.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                    }
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {

                    try {
                        lottie_webview_loading.visibility = View.GONE
                        webview_loading_text1.visibility = View.GONE
                        StyleableToast.makeText(
                            requireContext(),
                            "An error occurred",
                            R.style.customToast
                        ).show()
                    } catch (e: Exception) {
                    }
                }
            }
            article.url?.let {
                loadUrl(it)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (activity as NewsActivity).hideBottomNavigation()
        } catch (e: Exception) {
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        (activity as NewsActivity).hideBottomNavigation()
        super.onResume()
    }

    override fun onStop() {
        try {
            (activity as NewsActivity).supportActionBar?.show()
            (activity as NewsActivity).window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            (activity as NewsActivity).requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (e: Exception) {
        }
        super.onStop()
    }
}

