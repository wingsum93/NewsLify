package com.crushtech.newslify.ui.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
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
import com.crushtech.newslify.receiver.StreakReset
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
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
    var streakCount = 0

    // private var streakCount = 1
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


        view.findViewById<FloatingActionButton>(R.id.fab_favorite).setOnClickListener {
            //article.articleIsSaved = true
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

                SimpleCustomSnackbar.make(
                    viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                SimpleCustomSnackbar.make(
                    viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                    resetStreakAtMidnight()
                }

                if (isClicked && streakCount == 5) {
                    SimpleCustomSnackbar.make(
                        viewPos, "Daily news article goal reached :) ", Snackbar.LENGTH_LONG,
                        null, R.drawable.streak_icon,
                        "", ContextCompat.getColor(requireContext(), R.color.mygrey)
                    )?.show()

                } else if (isClicked && streakCount > 5 && streakCount % 5 == 0) {
                    SimpleCustomSnackbar.make(
                        viewPos,
                        "x${streakCount / 5} of daily goals reached: you're on fire ",
                        Snackbar.LENGTH_LONG,
                        null,
                        R.drawable.rocket,
                        "",
                        ContextCompat.getColor(requireContext(), R.color.mygrey)
                    )?.show()
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

    private fun resetStreakAtMidnight() {
        val intent = Intent(context, StreakReset::class.java)
        val mSrvcPendingingIntent: PendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent, 0
        )
        val c = Calendar.getInstance()
        val now = c.timeInMillis
        c.add(Calendar.DATE, 1)
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0

        val millisecondsUntilMidnight = c.timeInMillis - now

        val mAlarmManager: AlarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            millisecondsUntilMidnight,
            c.timeInMillis,
            mSrvcPendingingIntent

        )
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

