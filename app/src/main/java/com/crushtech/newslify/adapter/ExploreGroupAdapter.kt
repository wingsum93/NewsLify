package com.crushtech.newslify.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.GroupAdapter.SnapHelper.setUpSnapHelper
import com.crushtech.newslify.models.CustomZoomLayoutManager
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.fragments.ExploreSource
import com.crushtech.newslify.ui.fragments.exploreFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.explore_rv2_items.view.*

class ExploreGroupAdapter(
    private val exploreFragment: exploreFragment,
    val viewModel: NewsViewModel
) :
    RecyclerView.Adapter<ExploreGroupAdapter.EsViewHolder>() {

    var bbcNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Bbc News")
    var cnnNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Cnn News")
    var techCrunchnewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "TechCrunch News")
    var reutersNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Reuters News")
    var espnNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Espn News")
    var cnbcNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Cnbc News")
    var wsjNewsSource: ExploreBottomAdapter =
        ExploreBottomAdapter(exploreFragment, viewModel, "Wall Street Journal")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExploreGroupAdapter.EsViewHolder {
        return EsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.explore_rv2_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ExploreGroupAdapter.EsViewHolder, position: Int) {
        holder.itemView.apply {
            val items = differ.currentList[position]
            Picasso.get().load(items.img).fit().into(explore_source_img)
            explore_source_text.text = items.sourceName
            explore_source_motto.text = items.motto
        }
        setLists(holder.itemView.explore_source_rv, position)
    }

    private fun setLists(recyclerView: RecyclerView, position: Int) {
        //todo 4. Create a new adapter for it and display it in the list
        when (position) {
            0 -> setBbcSourceNews(recyclerView)
            1 -> setCnnSourceNews(recyclerView)
            2 -> setTechCrunchSourceNews(recyclerView)
            3 -> setReutersSourceNews(recyclerView)
            4 -> setEspnSourceNews(recyclerView)
            5 -> setCnbcSourceNews(recyclerView)
            6 -> setWsjSourceNews(recyclerView)

        }
    }


    private fun setBbcSourceNews(recyclerView: RecyclerView) {
        bbcNewsSource.setOnItemClickListener { article ->
            article.category = "Bbc News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bbcNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setCnnSourceNews(recyclerView: RecyclerView) {
        cnnNewsSource.setOnItemClickListener { article ->
            article.category = "Cnn News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cnnNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setTechCrunchSourceNews(recyclerView: RecyclerView) {
        techCrunchnewsSource.setOnItemClickListener { article ->
            article.category = "TechCrunch News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = techCrunchnewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setReutersSourceNews(recyclerView: RecyclerView) {
        reutersNewsSource.setOnItemClickListener { article ->
            article.category = "Reuters News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = reutersNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }


    private fun setEspnSourceNews(recyclerView: RecyclerView) {
        espnNewsSource.setOnItemClickListener { article ->
            article.category = "TechCrunch News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = espnNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setCnbcSourceNews(recyclerView: RecyclerView) {
        cnbcNewsSource.setOnItemClickListener { article ->
            article.category = "Cnbc News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cnbcNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }


    private fun setWsjSourceNews(recyclerView: RecyclerView) {
        wsjNewsSource.setOnItemClickListener { article ->
            article.category = "Wall Street Journal"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            exploreFragment.findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment,
                bundle
            )

        }
        recyclerView.apply {
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = wsjNewsSource
            (layoutManager as LinearLayoutManager).scrollToPosition(2)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ExploreSource>() {
        override fun areItemsTheSame(oldItem: ExploreSource, newItem: ExploreSource): Boolean {
            return oldItem.sourceName == newItem.sourceName
        }

        override fun areContentsTheSame(oldItem: ExploreSource, newItem: ExploreSource): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    inner class EsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}