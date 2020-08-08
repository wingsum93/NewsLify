package com.crushtech.newslify.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.BusinessNewsCoverAdapter
import com.crushtech.newslify.adapter.SearchNewsAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants
import com.crushtech.newslify.ui.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.search_loading_lottie
import kotlinx.coroutines.*


class searchNewsFragment : Fragment(R.layout.fragment_search_news), SearchView.OnQueryTextListener {
    private lateinit var viewModel: NewsViewModel
    private lateinit var searchNewsAdapter: SearchNewsAdapter

    private var queryText: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).showBottomNavigation()

        setUpRecyclerView()

        searchNewsAdapter.setOnItemClickListener { article ->
            article.category = "General News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideEmptySearchView()
                    hideProgressBar()

                    response.data?.let {
                        GlobalScope.launch(Dispatchers.Main) {
                            delay(4000)
                            searchNewsAdapter.differ.submitList(it.articles.toList())
                            searchNewsAdapter.showShimmer = false
                            searchNewsAdapter.notifyDataSetChanged()
                            val totalPages = it.totalResults / Constants.QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.searchNewsPage == totalPages
                            if (isLastPage) {
                                rvSearchNews.setPadding(0, 0, 0, 0)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideEmptySearchView()
                    searchNewsAdapter.showShimmer = false
                    hideProgressBar()
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            search_coordinator, it, Snackbar.LENGTH_SHORT, null,
                            R.drawable.network_off, "",
                            null
                        )?.show()
                    }
                    showEmptySearchView()
                }
                is Resource.Loading -> {
                    hideEmptySearchView()
                    //showProgressBar()
                }
            }
        })
        setHasOptionsMenu(true)
    }

    private fun setUpRecyclerView() {
        // newsAdapter = BreakingNewsAdapter()
        searchNewsAdapter = SearchNewsAdapter()
        searchNewsAdapter.showShimmer = false
        rvSearchNews.apply {
            adapter = searchNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(myScrollListener)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchNewsAdapter.differ.submitList(null)
        searchNewsAdapter.showShimmer = true
        hideEmptySearchView()
        viewModel.searchNewsResponse = null
        performSearch(query)
        queryText = query
        hideKeyboard()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }


    private fun performSearch(searchQuery: String?) {
        var job: Job? = null
        job?.cancel()
        job = MainScope().launch {
            delay(SEARCH_NEWS_TIME_DELAY)
            searchQuery?.let {
                if (searchQuery.isNotEmpty()) {
                    viewModel.getSearchNews(searchQuery)
                }

            }
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    private val myScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                if (TextUtils.isEmpty(queryText)) {
                    viewModel.searchNewsResponse = null
                    //showEmptySearchView()
                } else {
                    queryText?.let { viewModel.getSearchNews(it) }
                    showProgressBar()
                    hideEmptySearchView()
                    isScrolling = false
                }

            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView?
        searchView?.setOnQueryTextListener(this)
        searchView?.queryHint = "Search for news articles of any kind"

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideProgressBar() {
        search_loading_lottie.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        search_loading_lottie.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideEmptySearchView() {
        lottie_search_article.visibility = View.GONE
        search_article_text1.visibility = View.GONE
        search_article_text2.visibility = View.GONE
    }

    private fun showEmptySearchView() {
        lottie_search_article.visibility = View.VISIBLE
        search_article_text1.visibility = View.VISIBLE
        search_article_text2.visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        (activity as NewsActivity).showBottomNavigation()
        super.onAttach(context)
    }
}

