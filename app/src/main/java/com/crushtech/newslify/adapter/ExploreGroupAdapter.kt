package com.crushtech.newslify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.crushtech.newslify.R
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.fragments.ExploreSource
import com.crushtech.newslify.ui.fragments.exploreFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.explore_items.view.*
import kotlinx.android.synthetic.main.explore_layout.*
import kotlinx.android.synthetic.main.explore_rv2_items.view.*
import kotlinx.android.synthetic.main.group_item.view.*

class ExploreGroupAdapter(val context: Context) :
    RecyclerView.Adapter<ExploreGroupAdapter.EsViewHolder>() {
    var newsSource: ExploreBottomAdapter = ExploreBottomAdapter()
    var exploreItems: ExploreItemsAdapter = ExploreItemsAdapter()
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
            0 -> setDiscoverNews(recyclerView)
//            1 -> setSourceNews(recyclerView)
//            2 -> setSourceNews(recyclerView)
//            3 -> setSourceNews(recyclerView)
//            4 -> setSourceNews(recyclerView)

        }
    }

    private fun setDiscoverNews(recyclerView: RecyclerView) {
//        exploreItems.setOnItemClickListener {
//
//        }
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = exploreItems
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
        }
    }

    private fun setSourceNews(recyclerView: RecyclerView) {
        newsSource.setOnItemClickListener { article ->
//            article.category = "Sports"
//            val bundle = Bundle().apply {
//                putSerializable("article", article)
//            }
//            breakingNewsFragment.findNavController().navigate(
//                R.id.action_breakingNewsFragment_to_articleFragment,
//                bundle
//            )
        }
        recyclerView.apply {
            //  setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsSource
            isNestedScrollingEnabled = true
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