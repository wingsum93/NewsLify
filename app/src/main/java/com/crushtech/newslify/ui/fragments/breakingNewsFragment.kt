package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.GroupAdapter
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Resource
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class breakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var groups: ArrayList<Group>? = null
    lateinit var mAdView: AdView
    private lateinit var viewModel: NewsViewModel
    private val TAG = "BreakingNewsFragment"
    private var groupAdapter: GroupAdapter? = null
    private var mView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_breaking_news, container, false)
        }
        retainInstance = true
        return mView
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
        rvBreakingNews.visibility = View.VISIBLE
        rvBreakingNews.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setUpData() {
        viewModel.sportNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.sportNews.showShimmer = false
                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.sportNews.differ.submitList(
                                newsResponse.articles.toList().subList(0, 7)
                            )
                            groupAdapter!!.sportNews.notifyDataSetChanged()

                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            null
                        )?.show()

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
                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.businessNews.differ.submitList(
                                newsResponse.articles.toList().subList(0, 7)
                            )
                            groupAdapter!!.businessNews.notifyDataSetChanged()


                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            null
                        )?.show()
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

                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.entertainmentNews.differ.submitList(
                                newsResponse.articles.toList().subList(0, 8)
                            )
                            groupAdapter!!.entertainmentNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            null
                        )?.show()
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
                    response.data?.let { newsResponse ->

                        try {
                            groupAdapter!!.scienceNews.differ.submitList(
                                newsResponse.articles.toList().subList(0, 7)
                            )
                            groupAdapter!!.scienceNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            null
                        )?.show()
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
                    retry_connection.visibility = View.GONE
                    groupAdapter!!.breakingNews.showShimmer = false

                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.breakingNews.differ.submitList(
                                newsResponse.articles.toList().subList(0, 7)
                            )
                            groupAdapter!!.breakingNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    rvBreakingNews.visibility = View.INVISIBLE
                    lottie_no_internet.visibility = View.VISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            null
                        )?.show()
                    }
                    if (groupAdapter!!.differ.currentList.isEmpty()) {
                        retry_connection.visibility = View.VISIBLE
                    }
                    retry_connection.setOnClickListener {
                        if (viewModel.hasInternetConnection()) {
                            rvBreakingNews.visibility = View.VISIBLE
                            lottie_no_internet.visibility = View.GONE
                            viewModel.retryData()
                        } else {
                            SimpleCustomSnackbar.make(
                                brk_coordinator, "no network connection",
                                Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                                null
                            )?.show()
                        }
                    }
                }
                is Resource.Loading -> {
                    retry_connection.visibility = View.GONE
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(3000)
                    }
                }
            }
        })

    }


    override fun onAttach(context: Context) {
        (activity as NewsActivity).showBottomNavigation()
        super.onAttach(context)
    }

    override fun onResume() {
        (activity as NewsActivity).showBottomNavigation()
        super.onResume()
    }
}