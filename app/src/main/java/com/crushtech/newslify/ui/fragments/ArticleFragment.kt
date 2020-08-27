package com.crushtech.newslify.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.customized_article_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ArticleFragment : Fragment(), Picasso.Listener {
    private lateinit var viewModel: NewsViewModel
    private var isClicked = false
    private var btnCount = 0
    private var streakCount = 0
    private var loadinghasFinished = false
    private lateinit var mInterstitialAd: InterstitialAd

    private val args: ArticleFragmentArgs by navArgs()
    private var currentAnimator: AnimatorSet? = null
    private var shortAnimationDuration: Int = 0

    @SuppressLint("SetJavaScriptEnabled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setup transitions
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.customized_article_layout, container, false)
        viewModel = (activity as NewsActivity).newsViewModel
        val imageHolder = view.findViewById<ImageView>(R.id.breaking_news_image)
        retainInstance = true

        //get the argument from the generated arg class
        val article = args.article


        imageHolder.apply {
            transitionName = article.title
            setOnClickListener {
                hideOtherViews()
                zoomImageFromThumb(this, article.urlToImage, view)
            }
        }

        //Retrieve and cache the system default "short" animation time
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        try {
            if (article.urlToImage.isNullOrEmpty()) {
                Picasso.get().load(R.drawable.mylogo).fit().centerCrop().into(imageHolder)
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

                if (currentDay > previousDay || previousDay == 365 || previousDay == 366) {
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
                    val manager = ReviewManagerFactory.create(requireContext())
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { request ->
                        if (request.isSuccessful) {
                            // We got the ReviewInfo object
                            val reviewInfo = request.result
                            val flow =
                                manager.launchReviewFlow((activity as NewsActivity), reviewInfo)
                            flow.addOnCompleteListener { _ ->
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                            }
                        } else {
                            SimpleCustomSnackbar.make(
                                viewPos, "An unknown error occurred ", Snackbar.LENGTH_SHORT,
                                null, R.drawable.error_ic,
                                "", null
                            )?.show()
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
            try {
                val articleUrl = "From NewsLify:  ${article.url}"
                val shareSub = "APP NAME/TITLE"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/link"
                    putExtra(Intent.EXTRA_SUBJECT, shareSub)
                    putExtra(Intent.EXTRA_TEXT, articleUrl)
                }
                requireContext().startActivity(Intent.createChooser(intent, "Share Using"))
            } catch (e: ActivityNotFoundException) {
            }
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
                        SimpleCustomSnackbar.make(
                            requireView(), "an unknown error occurred",
                            Snackbar.LENGTH_SHORT, null, R.drawable.error_ic, "", null
                        )?.show()
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


    private fun zoomImageFromThumb(imageView: ImageView, articleImage: String?, myView: View) {
        // cancel if there's an animation already
        currentAnimator?.cancel()

        //load image
        val expandedImage = myView.findViewById<ImageView>(R.id.expandedImage)
        Picasso.get().load(articleImage).fit().centerCrop().into(expandedImage)


        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        imageView.getGlobalVisibleRect(startBoundsInt)
        myView.findViewById<View>(R.id.my_container)
            .getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float

        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        imageView.alpha = 0f
        expandedImage.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImage.pivotX = 0f
        expandedImage.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                    expandedImage,
                    View.X,
                    startBounds.left,
                    finalBounds.left
                )
            ).apply {
                with(
                    ObjectAnimator.ofFloat(
                        expandedImage,
                        View.Y,
                        startBounds.top,
                        finalBounds.top
                    )
                )
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        expandedImage.apply {
            setOnClickListener {
                showOtherViews()
                setupAnimator(startBounds, startScale, imageView)
            }
            setOnDragListener(object : View.OnDragListener {
                override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
                    if (p1?.action == DragEvent.ACTION_DRAG_ENTERED || p1?.action == DragEvent.ACTION_DRAG_ENDED) {
                        showOtherViews()
                        setupAnimator(startBounds, startScale, imageView)
                        return true
                    }
                    return false
                }

            })
            setOnLongClickListener {
                showOtherViews()
                setupAnimator(startBounds, startScale, imageView)
                true
            }
        }

    }

    private fun showOtherViews() {
        extendArticleView.visibility = View.VISIBLE
        nestedScrollView.visibility = View.VISIBLE
        scrollwebView1.visibility = View.VISIBLE
        fab_favorite1.visibility = View.VISIBLE
        fab_share1.visibility = View.VISIBLE
        lottie_webview_loading1.visibility = View.VISIBLE
        webview_loading_text2.visibility = View.VISIBLE
    }

    private fun hideOtherViews() {
        extendArticleView.visibility = View.INVISIBLE
        nestedScrollView.visibility = View.INVISIBLE
        scrollwebView1.visibility = View.INVISIBLE
        fab_favorite1.visibility = View.INVISIBLE
        fab_share1.visibility = View.INVISIBLE
        lottie_webview_loading1.visibility = View.INVISIBLE
        webview_loading_text2.visibility = View.INVISIBLE
    }

    private fun setupAnimator(startBounds: RectF, startScale: Float, imageView: ImageView) {

        currentAnimator?.cancel()

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        currentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(expandedImage, View.X, startBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandedImage, View.Y, startBounds.top))
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale))
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    imageView.alpha = 1f
                    expandedImage.visibility = View.GONE
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
//                val typedValue = TypedValue()
//                activity!!.theme
//                    .resolveAttribute(R.attr.mainBackground, typedValue, true)
//                if (typedValue.resourceId != 0) {
//                    article_parent.setBackgroundResource(typedValue.resourceId)
//                } else {
//                    article_parent.setBackgroundColor(typedValue.data)
//                }
                    imageView.alpha = 1f
                    expandedImage.visibility = View.GONE
                    currentAnimator = null
                }
            })
            start()
        }
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

    override fun onImageLoadFailed(picasso: Picasso?, uri: Uri?, exception: java.lang.Exception?) {
        startPostponedEnterTransition()
    }
}

