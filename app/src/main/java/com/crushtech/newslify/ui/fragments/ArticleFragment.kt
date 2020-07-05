package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
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
        val article = args.article

        view.findViewById<FloatingActionButton>(R.id.fab_favorite).setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).apply {
                setAction("View") {
                    findNavController().navigate(R.id.action_articleFragment_to_savedNewsFragment)
                }
                show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            Snackbar.make(view, "share button clicked", Snackbar.LENGTH_SHORT).show()
        }

        val bottomSheet: NestedScrollView = view.findViewById(R.id.my_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val articleImg = view.findViewById<ImageView>(R.id.article_img)
        article.urlToImage?.let { image ->
            Picasso.get().load(image).fit().centerCrop()
                .into(articleImg)
        }
        view.findViewById<WebView>(R.id.scrollwebView).apply {

            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  (activity as NewsActivity).supportActionBar?.hide()
        //get the argument from the generated arg class
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as NewsActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as NewsActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onStop() {
        (activity as NewsActivity).showBottomNavigation()
        (activity as NewsActivity).supportActionBar?.show()
        (activity as NewsActivity).window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onStop()
    }

}