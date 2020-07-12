package com.crushtech.newslify.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
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
        //get the argument from the generated arg class
        val article = args.article
        val viewPos: View = view.findViewById(R.id.myCord)
        view.findViewById<FloatingActionButton>(R.id.fab_favorite).setOnClickListener {
            viewModel.saveArticle(article)

            val customSnackListener: View.OnClickListener = View.OnClickListener {
                findNavController().navigate(R.id.action_articleFragment_to_savedNewsFragment)
            }
            SimpleCustomSnackbar.make(
                viewPos, "Article saved successfully", Snackbar.LENGTH_LONG,
                customSnackListener, R.drawable.snack_fav,
                "View", ContextCompat.getColor(requireContext(), R.color.mygrey)
            )?.show()
        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (activity as NewsActivity).hideBottomNavigation()
        } catch (e: Exception) {
        }

    }

    override fun onResume() {
        (activity as NewsActivity).hideBottomNavigation()
        super.onResume()
    }

    override fun onStop() {
        try {
            // (activity as NewsActivity).showBottomNavigation()
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