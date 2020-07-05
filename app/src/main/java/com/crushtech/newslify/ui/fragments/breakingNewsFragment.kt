package com.crushtech.newslify.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.GroupAdapter
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.crushtech.newslify.ui.util.Resource
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.group_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

class breakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var groups: ArrayList<Group>? = null
    private lateinit var viewModel: NewsViewModel
    private val TAG = "BreakingNewsFragment"
    private var groupAdapter: GroupAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()

    }

    private fun initGroupData() {
        groups = ArrayList()
        groups!!.add(Group("Sports News", "View All"))
        groups!!.add(Group("Business News", "View All"))
        groups!!.add(Group("Entertainment News", "View All"))
        groups!!.add(Group("Science News", "View All"))
        groups!!.add(Group("Breaking News", "View All"))
    }


    private fun setUpRecyclerView() {

        initGroupData()
        setUpData()
        groupAdapter = GroupAdapter(
            requireContext(),
            this
        )
        groupAdapter!!.differ.submitList(groups!!)
        rvBreakingNews.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(activity)
            // addOnScrollListener(myScrollListener)
        }

    }

    private fun setUpData() {
        viewModel.sportNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.sportNews.showShimmer = false
                    reconnect_btn.visibility = View.INVISIBLE
                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.sportNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.sportNews.notifyDataSetChanged()

                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_SHORT)
                            .show()
                        reconnectOnNoConnection()

                    }
                }
                is Resource.Loading -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

        viewModel.businessNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.businessNews.showShimmer = false
                    reconnect_btn.visibility = View.INVISIBLE
                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.businessNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.businessNews.notifyDataSetChanged()


                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_SHORT)
                            .show()
                        reconnectOnNoConnection()
                    }
                }
                is Resource.Loading -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

        viewModel.entertainmentNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.entertainmentNews.showShimmer = false
                    reconnect_btn.visibility = View.INVISIBLE

                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.entertainmentNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.entertainmentNews.notifyDataSetChanged()
                            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.allBreakingNewsPage == totalPages
//                            if (isLastPage) {
//                                rvBreakingNews.setPadding(0, 0, 0, 0)
//                            }
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_SHORT)
                            .show()

                        reconnectOnNoConnection()
                    }
                }
                is Resource.Loading -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

        viewModel.scienceNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.scienceNews.showShimmer = false
                    reconnect_btn.visibility = View.INVISIBLE
                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.scienceNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.scienceNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_SHORT)
                            .show()
                        reconnectOnNoConnection()
                    }
                }
                is Resource.Loading -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

        viewModel.allBreakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.breakingNews.showShimmer = false
                    reconnect_btn.visibility = View.INVISIBLE

                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.breakingNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.breakingNews.notifyDataSetChanged()
                            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.allBreakingNewsPage == totalPages
                            if (isLastPage) {
                                rvBreakingNews.setPadding(0, 0, 0, 0)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_SHORT)
                            .show()
                        reconnectOnNoConnection()
                    }
                }
                is Resource.Loading -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

    }

    private fun reconnectOnNoConnection() {
        reconnect_btn.visibility = View.VISIBLE
        reconnect_btn.setOnClickListener {
            setUpData()
        }
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val myScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling

//            if (shouldPaginate) {
//                viewModel.getAllBreakingNews("us")
//                isScrolling = false
//            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}