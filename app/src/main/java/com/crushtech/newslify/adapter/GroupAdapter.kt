package com.crushtech.newslify.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.GroupAdapter.SnapHelper.setUpSnapHelper
import com.crushtech.newslify.models.CustomZoomLayoutManager
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.ui.fragments.breakingNewsFragment
import kotlinx.android.synthetic.main.group_item.view.*


class GroupAdapter
    (
    private val breakingNewsFragment: breakingNewsFragment
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {
    var businessNews: BusinessNewsCoverAdapter = BusinessNewsCoverAdapter()
    var breakingNews: BreakingNewsAdapter = BreakingNewsAdapter()
    var sportNews: SportNewsAdapter = SportNewsAdapter()
    var scienceNews: ScienceNewsAdapter = ScienceNewsAdapter()
    var entertainmentNews: EntertainmentNewsAdapter = EntertainmentNewsAdapter()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = differ.currentList[position]

        group.groupButtonTitle?.let {
            setGroupButtonTitle(holder.view_all, it)
        }
        group.groupTitle?.let {
            setGroupTitle(holder.group_title, it)
        }
        group.groupTitle?.let {
            holder.setOnClickViewAll(it)

        }

        setLists(holder.itemView.group_recycler_view, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun setGroupTitle(textView: TextView, text: String) {
        textView.text = text
    }

    private fun setGroupButtonTitle(textView: TextView, text: String) {
        textView.text = text
    }


    private fun setLists(recyclerView: RecyclerView, position: Int) {

        when (position) {
            0 -> setSportList(recyclerView)
            1 -> setBusinessList(recyclerView)
            2 -> setEntertainmentList(recyclerView)
            3 -> setScienceList(recyclerView)
            4 -> setBreakingNews(recyclerView)

        }
    }

    private fun setSportList(recyclerView: RecyclerView) {
        sportNews.setOnItemClickListener { transitionView, article ->
            article.category = "Sports"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            breakingNewsFragment.findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle,
                null,
                FragmentNavigatorExtras(
                    transitionView to article.title.toString()
                )
            )
        }
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = sportNews
            isNestedScrollingEnabled = true
            isSaveEnabled = true
        }

    }

    private fun setBusinessList(recyclerView: RecyclerView) {
        businessNews.setOnItemClickListener { transitionView, article ->
            article.category = "Business"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            breakingNewsFragment.findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle,
                null,
                FragmentNavigatorExtras(
                    transitionView to article.title.toString()
                )
            )
        }
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                CustomZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = businessNews
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
            isNestedScrollingEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setEntertainmentList(recyclerView: RecyclerView) {
        entertainmentNews.setOnItemClickListener { transitionView, article ->
            article.category = "Entertainment"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            breakingNewsFragment.findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle,
                null,
                FragmentNavigatorExtras(
                    transitionView to article.title.toString()
                )
            )
        }
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
            adapter = entertainmentNews
            isNestedScrollingEnabled = true
            isSaveEnabled = true
        }
    }

    private fun setScienceList(recyclerView: RecyclerView) {
        scienceNews.setOnItemClickListener { transitionView, article ->
            article.category = "Science"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            breakingNewsFragment.findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle,
                null,
                FragmentNavigatorExtras(
                    transitionView to article.title.toString()
                )
            )
        }
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                CustomZoomLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = scienceNews
            (layoutManager as LinearLayoutManager).scrollToPosition(0)
            isNestedScrollingEnabled = true
            isSaveEnabled = true
            post {
                setUpSnapHelper(recyclerView)
            }
        }
    }

    private fun setBreakingNews(recyclerView: RecyclerView) {
        breakingNews.setOnItemClickListener { transitionView, article ->
            article.category = "General News"
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            breakingNewsFragment.findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle,
                null,
                FragmentNavigatorExtras(
                    transitionView to article.title.toString()
                )
            )
        }
        recyclerView.apply {
            adapter = breakingNews
            layoutManager = LinearLayoutManager(recyclerView.context)
            isNestedScrollingEnabled = true
            isSaveEnabled = true
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupTitle == newItem.groupTitle
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var head_parent: RelativeLayout = itemView.findViewById(R.id.head_parent)
        var group_title: TextView = itemView.findViewById(R.id.group_title)
        var view_all: TextView = itemView.findViewById(R.id.view_all)
        var group_recycler_view: RecyclerView = itemView.findViewById(R.id.group_recycler_view)

        fun setOnClickViewAll(groupTitle: String) {
            view_all.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("groupTitle", groupTitle)
                }
                breakingNewsFragment.findNavController()
                    .navigate(
                        R.id.action_breakingNewsFragment_to_viewAllFragment,
                        bundle
                    )
            }
        }
    }

    object SnapHelper {
        fun setUpSnapHelper(recyclerView: RecyclerView) {
            if (recyclerView.getChildAt(0) != null) {
                val dx = (recyclerView.width - recyclerView.getChildAt(0).width) / 2
                recyclerView.scrollBy(-dx, 0)
                // Assign the LinearSnapHelper that will initially snap the near-center view.
                val snapHelper = LinearSnapHelper()
                if (recyclerView.onFlingListener == null)
                    snapHelper.attachToRecyclerView(recyclerView)
            }
        }
    }

}