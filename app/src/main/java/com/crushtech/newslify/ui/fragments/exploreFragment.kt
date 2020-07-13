package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.ExploreItemsAdapter
import com.crushtech.newslify.adapter.ExploreGroupAdapter
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.explore_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

data class Explore(val name: String, val imageSource: Int, val itemBackground: Int)
data class ExploreSource(val sourceName: String, val img: Int, val motto: String)

private var explore: ArrayList<Explore>? = null
private var exploreSource: ArrayList<ExploreSource>? = null

private var exploreGroupAdapter: ExploreGroupAdapter? = null
private var exploreItemsAdapter: ExploreItemsAdapter? = null
private lateinit var viewModel: NewsViewModel

class exploreFragment : Fragment(R.layout.explore_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        (activity as NewsActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpRecView()
        setUpRecyclerViewForSource()
        retainInstance = true
        exploreItemsAdapter!!.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("exploreName", it.name)
            }
            findNavController().navigate(
                R.id.action_exploreFragment_to_exploreTopItemsFragment,
                bundle
            )
        }
    }

    private fun initExploreItems() {
        explore = ArrayList()
        explore!!.add(
            Explore(
                "Technology",
                R.drawable.technology_icon,
                R.drawable.explore_item1_bg
            )
        )
        explore!!.add(Explore("Sports", R.drawable.sports_icon, R.drawable.explore_item2_bg))
        explore!!.add(Explore("Business", R.drawable.business_icon, R.drawable.explore_item3_bg))
        explore!!.add(
            Explore(
                "Entertainment",
                R.drawable.entertainment_icon,
                R.drawable.explore_item4_bg
            )
        )
        explore!!.add(Explore("Health", R.drawable.health_icon, R.drawable.explore_item5_bg))
        explore!!.add(Explore("Science", R.drawable.science_icon, R.drawable.explore_item6_bg))
    }

    private fun setUpRecView() {
        initExploreItems()
        exploreItemsAdapter = ExploreItemsAdapter()
        exploreItemsAdapter!!.differ.submitList(explore!!)
        explore_rv1.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = exploreItemsAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }
    }

    private fun setUpRecyclerViewForSource() {
        initExploreSourceItems()
        setUpData()
        exploreGroupAdapter = ExploreGroupAdapter(
            requireContext(), this, viewModel
        )
        exploreGroupAdapter!!.differ.submitList(exploreSource)
        explore_rv2.apply {
            adapter = exploreGroupAdapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initExploreSourceItems() {
        exploreSource = ArrayList()
        exploreSource!!.add(ExploreSource("Bbc News", R.drawable.bbc, "The world's newsroom"))
        exploreSource!!.add(ExploreSource("Cnn News", R.drawable.cnn, "The world's news network"))
        exploreSource!!.add(
            ExploreSource(
                "TechCrunch",
                R.drawable.techcrunch,
                "Dronepocalypse Now"
            )
        )
        exploreSource!!.add(
            ExploreSource(
                "Reuters",
                R.drawable.reuters,
                "Reuters Trust Principles"
            )
        )
        exploreSource!!.add(
            ExploreSource(
                "Espn News",
                R.drawable.espn,
                "The worldwide leader in sports"
            )
        )
        exploreSource!!.add(
            ExploreSource(
                "Cnbc News",
                R.drawable.cnbc,
                "First in business worldwide"
            )
        )
        exploreSource!!.add(
            ExploreSource(
                "Wall Street Journal",
                R.drawable.wsj,
                "free markets and free people"
            )
        )
    }

    private fun setUpData() {
        viewModel.specificNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    //groupAdapter!!.sportNews.showShimmer = false
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.bbcNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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


        viewModel.specificNews1.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    //groupAdapter!!.sportNews.showShimmer = false
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.cnnNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                            //groupAdapter!!.sportNews.notifyDataSetChanged()

                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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

        viewModel.specificNews2.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    //groupAdapter!!.sportNews.showShimmer = false
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.techCrunchnewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                            //groupAdapter!!.sportNews.notifyDataSetChanged()

                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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


        viewModel.specificNews3.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.reutersNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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


        viewModel.specificNews4.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.espnNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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


        viewModel.specificNews5.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.cnbcNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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

        viewModel.specificNews6.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        try {
                            exploreGroupAdapter!!.wsjNewsSource.differ.submitList(
                                newsResponse.articles.toList().shuffled()
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                is Resource.Error -> {
                    explore_rv2.visibility = View.INVISIBLE
                    response.message?.let {
                        SimpleCustomSnackbar.make(
                            requireView(), "an error occurred :$it",
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

    override fun onStop() {
        try {
            (activity as NewsActivity).requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (e: Exception) {
        }
        super.onStop()
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