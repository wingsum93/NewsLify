package com.crushtech.newslify.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsActivity
import kotlinx.android.synthetic.main.group_item.view.*

class ExploreTopItemsFragment : Fragment(R.layout.fragment_view_all_news) {
    private val args: ExploreTopItemsFragmentArgs by navArgs()
    private var exploreItemCategory = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exploreItemCategory = args.exploreName
        (activity as NewsActivity).supportActionBar?.title = exploreItemCategory
    }
}