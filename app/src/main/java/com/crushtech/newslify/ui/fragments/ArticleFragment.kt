package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.entertainment_news.view.*
import kotlinx.android.synthetic.main.fragment_article.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ArticleFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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
                "View", ContextCompat.getColor(requireContext(), R.color.mycolor)
            )?.show()
        }

        view.findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            SimpleCustomSnackbar.make(
                viewPos, "Article shared", Snackbar.LENGTH_LONG,
                null, R.drawable.ic_share,
                "", ContextCompat.getColor(requireContext(), R.color.mycolor)
            )?.show()
        }

        val bottomSheet: NestedScrollView = view.findViewById(R.id.my_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<WebView>(R.id.scrollwebView).apply {

            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
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