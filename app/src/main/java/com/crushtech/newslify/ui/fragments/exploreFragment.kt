package com.crushtech.newslify.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.ExploreItemsAdapter
import com.crushtech.newslify.adapter.GroupAdapter
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.ui.NewsActivity
import kotlinx.android.synthetic.main.explore_layout.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import java.util.ArrayList

data class Explore(val name: String, val imageSource: Int,val itemBackground:Int)

private var explore: ArrayList<Explore>? = null
private var exploreItemsAdapter: ExploreItemsAdapter? = null

class exploreFragment : Fragment(R.layout.explore_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NewsActivity).newsViewModel
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        initExploreItems()
        exploreItemsAdapter = ExploreItemsAdapter()
        exploreItemsAdapter!!.differ.submitList(explore!!)
        explore_rv.apply {
            adapter = exploreItemsAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
            setHasFixedSize(true)
            // addOnScrollListener(myScrollListener)
        }
    }

    private fun initExploreItems() {
        explore = ArrayList()
        explore!!.add(Explore("Technology", R.drawable.technology_icon,R.drawable.explore_item1_bg))
        explore!!.add(Explore("Sports", R.drawable.sports_icon,R.drawable.explore_item2_bg))
        explore!!.add(Explore("Business", R.drawable.business_icon,R.drawable.explore_item3_bg))
        explore!!.add(Explore("Entertainment", R.drawable.entertainment_icon,R.drawable.explore_item4_bg))
        explore!!.add(Explore("Health", R.drawable.health_icon,R.drawable.explore_item5_bg))
        explore!!.add(Explore("Science", R.drawable.science_icon,R.drawable.explore_item6_bg))

    }
}