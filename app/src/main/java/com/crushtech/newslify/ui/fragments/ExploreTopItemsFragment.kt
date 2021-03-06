package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.ExploreTopItemAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.fragment_explore_top_items_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class ExploreTopItemsFragment : Fragment(R.layout.fragment_explore_top_items_news) {
    private val args: ExploreTopItemsFragmentArgs by navArgs()
    private var exploreItemCategory = ""
    var shouldPaginate = false
    private lateinit var viewModel: NewsViewModel
    private lateinit var exploreTopItemAdapter: ExploreTopItemAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exploreItemCategory = args.exploreName
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).supportActionBar?.title = exploreItemCategory
        retainInstance = true
        setUpRecyclerView()

        exploreTopItemAdapter.setOnItemClickListener { article ->
            article.category = exploreItemCategory
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_exploreTopItemsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun setUpRecyclerView() {
        setUpViewAllMagic()
        exploreTopItemAdapter = ExploreTopItemAdapter()
        exploreTopItemAdapter.showShimmer = false
        rvexploreTop.apply {
            adapter = exploreTopItemAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(myScrollListener)
        }
    }


    private fun setUpViewAllMagic() {
        val getCountryCode =
            requireContext().getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val countryIsoCode = getCountryCode.getString("countryIsoCode", "us")
        when (exploreItemCategory) {
            "Technology" -> {
                viewModel.technologyNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        exploreTopItemAdapter.differ.submitList(
                                            newsResponse.articles.toList()
                                        )
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
                            }
                        }
                    })
            }

            "Business" -> {
                viewModel.getBusinessNews("us", "business")
                viewModel.businessNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        if (countryIsoCode!!.contains("us")) {
                                            exploreTopItemAdapter.differ.submitList(newsResponse.articles.toList())
                                        } else {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList().drop(20)
                                            )
                                        }
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
                            }
                        }
                    })
            }

            "Sports" -> {
                viewModel.getSportNews("us", "sport")
                viewModel.sportNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        if (countryIsoCode == "us") {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList()
                                            )
                                        } else {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList().drop(20)
                                            )
                                        }
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
                            }
                        }
                    })
            }

            "Entertainment" -> {
                context?.let { viewModel.getEntertainmentNews("us", "entertainment", it) }
                viewModel.entertainmentNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        if (countryIsoCode == "us") {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList()
                                            )
                                        } else {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList().drop(20)
                                            )
                                        }
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
                            }
                        }
                    })
            }

            "Health" -> {
                viewModel.getHealthNews("us", "health")
                viewModel.healthNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        exploreTopItemAdapter.differ.submitList(newsResponse.articles.toList())
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
                            }
                        }
                    })
            }

            "Science" -> {
                viewModel.getScienceNews("us", "science")
                viewModel.scienceNews.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreTopItemAdapter.showShimmer = false
                                response.data?.let { newsResponse ->
                                    try {
                                        if (countryIsoCode == "us") {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList()
                                            )
                                        } else {
                                            exploreTopItemAdapter.differ.submitList(
                                                newsResponse.articles.toList().drop(20)
                                            )
                                        }
                                        exploreTopItemAdapter.notifyDataSetChanged()
                                        val totalPages =
                                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                                        isLastPage = viewModel.specificNewsPage == totalPages
                                        if (isLastPage) {
                                            rvexploreTop.setPadding(0, 0, 0, 0)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                            is Resource.Error -> {
                                response.message?.let {
                                    SimpleCustomSnackbar.make(
                                        explore_top_coordinator, it,
                                        Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                        null
                                    )?.show()

                                }
                            }
                            is Resource.Loading -> {
                                StyleableToast.makeText(
                                    requireContext(),
                                    "loading please wait", R.style.customToast1
                                ).show()
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
        explore_top_loading_lottie.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        explore_top_loading_lottie.visibility = View.VISIBLE
        isLoading = true
    }


    override fun onAttach(context: Context) {
        (activity as NewsActivity).hideBottomNavigation()
        super.onAttach(context)
    }

}
