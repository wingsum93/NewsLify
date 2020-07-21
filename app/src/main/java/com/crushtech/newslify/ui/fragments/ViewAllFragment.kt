package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.ViewAllAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_view_all_news.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

class ViewAllFragment : Fragment(R.layout.fragment_view_all_news) {
    private lateinit var viewModel: NewsViewModel
    private lateinit var viewAllAdapter: ViewAllAdapter
    private var articleCategory = ""
    var shouldPaginate = false
    private var countryIsoCode: String? = null

    //for view all title
    private val args: ViewAllFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        articleCategory = args.groupTitle
        (activity as NewsActivity).supportActionBar?.title = articleCategory

        setUpRecyclerView()
        retainInstance = true
        viewAllAdapter.setOnItemClickListener { article ->
            article.category = articleCategory
            val bundle = Bundle().apply {
                putSerializable("article", article)

            }
            findNavController().navigate(
                R.id.action_viewAllFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun setUpRecyclerView() {
        setUpViewAllMagic()
        viewAllAdapter = ViewAllAdapter()
        viewAllAdapter.showShimmer = false
        rvViewAllNews.apply {
            adapter = viewAllAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(myScrollListener)
        }
    }

    private fun setUpViewAllMagic() {
        val getCountryCode =
            requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        countryIsoCode = getCountryCode.getString("countryIsoCode", "us")

        when (articleCategory) {
            "Entertainment News" -> {
                viewModel.getEntertainmentNews(
                    countryIsoCode!!.toLowerCase(Locale.ROOT),
                    "entertainment", requireContext()
                )
                viewModel.entertainmentNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                viewAllAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        viewAllAdapter.differ.submitList(newsResponse.articles.toList())
                                        viewAllAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.entertainmentNewsPage == totalPages
                                        if (isLastPage) {
                                            rvViewAllNews.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        view_all_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(2000)
                                }
                            }
                        }
                    })
            }
            "Sports News" -> {
                viewModel.getSportNews(
                    countryIsoCode!!.toLowerCase(Locale.ROOT),
                    "sport"
                )
                viewModel.sportNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                viewAllAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        viewAllAdapter.differ.submitList(newsResponse.articles.toList())
                                        viewAllAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.sportNewsPage == totalPages
                                        if (isLastPage) {
                                            rvViewAllNews.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        view_all_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(2000)
                                }
                            }
                        }
                    })
            }
            "Science News" -> {
                viewModel.getScienceNews(
                    countryIsoCode!!.toLowerCase(Locale.ROOT),
                    "science"
                )
                viewModel.scienceNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                viewAllAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        viewAllAdapter.differ.submitList(newsResponse.articles.toList())
                                        viewAllAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.scienceNewsPage == totalPages
                                        if (isLastPage) {
                                            rvViewAllNews.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        view_all_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(2000)
                                    showProgressBar()
                                }
                            }
                        }
                    })
            }
            "Business News" -> {
                viewModel.getBusinessNews(
                    countryIsoCode!!.toLowerCase(Locale.ROOT),
                    "business"
                )
                viewModel.businessNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                viewAllAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        viewAllAdapter.differ.submitList(newsResponse.articles.toList())
                                        viewAllAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.businessNewsPage == totalPages
                                        if (isLastPage) {
                                            rvViewAllNews.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        view_all_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(2000)
                                }
                            }
                        }
                    })
            }
            else -> {
                viewModel.getAllBreakingNews(countryIsoCode!!.toLowerCase(Locale.ROOT))
                viewModel.allBreakingNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                viewAllAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        viewAllAdapter.differ.submitList(newsResponse.articles.toList())
                                        viewAllAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.allBreakingNewsPage == totalPages
                                        if (isLastPage) {
                                            rvViewAllNews.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        view_all_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(2000)
                                }
                            }
                        }
                    })
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
            shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                setUpViewAllMagic()
                showProgressBar()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun hideProgressBar() {
        viewall_loading_lottie.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        viewall_loading_lottie.visibility = View.VISIBLE
        isLoading = true
    }


    override fun onAttach(context: Context) {
        (activity as NewsActivity).hideBottomNavigation()
        super.onAttach(context)
    }


}