package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.GroupAdapter
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class breakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var groups: ArrayList<Group>? = null
    private lateinit var viewModel: NewsViewModel
    private val TAG = "BreakingNewsFragment"
    private var groupAdapter: GroupAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()
        retainInstance = true

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
        }

    }

    private fun setUpData() {
        val getCountryCode =
            context?.getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val countryIsoCode = getCountryCode?.getString("countryIsoCode", "us")

        if (countryIsoCode != null) {
            viewModel.getSportNews(countryIsoCode.toLowerCase(Locale.ROOT), "sport")
            viewModel.getBusinessNews(countryIsoCode.toLowerCase(Locale.ROOT), "business")
            viewModel.getAllBreakingNews(countryIsoCode.toLowerCase(Locale.ROOT))
            viewModel.getEntertainmentNews(
                countryIsoCode.toLowerCase(Locale.ROOT), "entertainment",
                requireContext()
            )
            viewModel.getScienceNews(countryIsoCode.toLowerCase(Locale.ROOT), "science")
        }
        viewModel.sportNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    groupAdapter!!.sportNews.showShimmer = false
                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.sportNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.sportNews.notifyDataSetChanged()

                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    no_internet_text.visibility = View.VISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                            groupAdapter!!.businessNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.businessNews.notifyDataSetChanged()


                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    no_internet_text.visibility = View.VISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                            groupAdapter!!.entertainmentNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.entertainmentNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    no_internet_text.visibility = View.VISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                            groupAdapter!!.scienceNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.scienceNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    lottie_no_internet.visibility = View.VISIBLE
                    no_internet_text.visibility = View.VISIBLE
                    rvBreakingNews.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            ContextCompat.getColor(requireContext(), R.color.mygrey)
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
                    groupAdapter!!.breakingNews.showShimmer = false

                    response.data?.let { newsResponse ->
                        try {
                            groupAdapter!!.breakingNews.differ.submitList(newsResponse.articles.toList())
                            groupAdapter!!.breakingNews.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    rvBreakingNews.visibility = View.INVISIBLE
                    lottie_no_internet.visibility = View.VISIBLE
                    no_internet_text.visibility = View.VISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            brk_coordinator, "an error occurred: $it",
                            Snackbar.LENGTH_SHORT, null, R.drawable.network_off, "",
                            ContextCompat.getColor(requireContext(), R.color.mygrey)
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