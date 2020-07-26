package com.crushtech.newslify.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.customized_article_layout.*
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
    private var loadinghasFinished = false
    private lateinit var mInterstitialAd: InterstitialAd

    private val args: ArticleFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.customized_article_layout, container, false)
        viewModel = (activity as NewsActivity).newsViewModel
        val imageHolder = view.findViewById<ImageView>(R.id.articleimages)
        retainInstance = true

        //get the argument from the generated arg class
        val article = args.article
        try {
            if (article.urlToImage.isNullOrEmpty()) {
                imageHolder.setBackgroundResource(R.drawable.mylogo)
            } else {
                Picasso.get().load(article.urlToImage).fit().centerCrop()
                    .into(imageHolder)
            }
        } catch (e: Exception) {
        }
        val viewPos: View = view.findViewById(R.id.myCord1)
        val logoAnim: Animation = AnimationUtils.loadAnimation(
            context,
            android.R.anim.fade_in
        )
        val slideInAnim: Animation = AnimationUtils.loadAnimation(
            context,
            R.anim.button_anim
        )

        MobileAds.initialize(context) {}

        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = "ca-app-pub-7292512767354152/3859686593"
        val adRequest1: AdRequest = AdRequest.Builder().build()
        mInterstitialAd.loadAd(adRequest1)

        val collapseArticleView =
            view.findViewById<ExtendedFloatingActionButton>(R.id.collapseArticleView)
        val extendArticleView =
            view.findViewById<ExtendedFloatingActionButton>(R.id.extendArticleView)
        collapseArticleView.animation = slideInAnim
        extendArticleView.animation = slideInAnim
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollView)
        //extend article logic
        extendArticleView.setOnClickListener {
            (activity as NewsActivity).supportActionBar?.hide()
            imageHolder.visibility = View.GONE
            nestedScrollView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            nestedScrollView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            extendArticleView.visibility = View.GONE
            // collapseArticleView.visibility = View.VISIBLE
        }
        //collapse article logic
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?,
                                                     scrollX: Int,
                                                     scrollY: Int,
                                                     oldScrollX: Int,
                                                     oldScrollY: Int ->
            if (scrollY > oldScrollX) {
                collapseArticleView.visibility = View.GONE
            }
            if (scrollY < oldScrollY && !(extendArticleView.isVisible)) {
                collapseArticleView.visibility = View.VISIBLE
            }

        }
        val dpInPixels =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600f, resources.displayMetrics)
        collapseArticleView.setOnClickListener {
            (activity as NewsActivity).supportActionBar?.show()
            imageHolder.visibility = View.VISIBLE
            nestedScrollView.layoutParams.height = dpInPixels.toInt()
            nestedScrollView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            extendArticleView.visibility = View.VISIBLE
            collapseArticleView.visibility = View.GONE
        }

        view.findViewById<FloatingActionButton>(R.id.fab_favorite1).setOnClickListener {
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

                view.findViewById<FloatingActionButton>(R.id.fab_favorite1).apply {
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                    startAnimation(logoAnim)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        lottie_saved_anim1.visibility = View.VISIBLE
                        lottie_webview_loading1.visibility = View.INVISIBLE
                        webview_loading_text2.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                    }
                    delay(3000L)
                    try {
                        lottie_saved_anim1.visibility = View.GONE
                        //check if loading lottie has already appeared and has finished loading
                        if (!loadinghasFinished) {
                            lottie_webview_loading1.visibility = View.VISIBLE
                            webview_loading_text2.visibility = View.VISIBLE
                        }
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
                lottie_saved_anim1.visibility = View.GONE
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


        view.findViewById<FloatingActionButton>(R.id.fab_share1).setOnClickListener {
            val articleUrl = "From NewsLify:  ${article.url}"
            val shareSub = "APP NAME/TITLE"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/link"
                putExtra(Intent.EXTRA_SUBJECT, shareSub)
                putExtra(Intent.EXTRA_TEXT, articleUrl)
            }
            startActivity(Intent.createChooser(intent, "Share Using"))
        }

        view.findViewById<WebView>(R.id.scrollwebView1).apply {
            this.settings.cacheMode = WebSettings.LOAD_DEFAULT
            this.settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        loadinghasFinished = true
                        lottie_webview_loading1.visibility = View.INVISIBLE
                        webview_loading_text2.visibility = View.INVISIBLE
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
                        loadinghasFinished = true
                        lottie_webview_loading1.visibility = View.GONE
                        webview_loading_text2.visibility = View.GONE
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


    override fun onStop() {
        (activity as NewsActivity).supportActionBar?.show()
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        (activity as NewsActivity).hideBottomNavigation()
        super.onResume()
    }


}

