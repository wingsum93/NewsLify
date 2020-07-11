package com.crushtech.newslify.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.Group
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.fragments.Explore
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.explore_items.view.*

class ExploreItemsAdapter :
    RecyclerView.Adapter<ExploreItemsAdapter.ExploreItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExploreItemsAdapter.ExploreItemViewHolder {
        return ExploreItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.explore_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: ExploreItemsAdapter.ExploreItemViewHolder,
        position: Int
    ) {
        holder.itemView.apply {
            val logoAnim: Animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.fade_in
            )
            val items = differ.currentList[position]
            explore_text.text = items.name
            explore_img.setImageResource(items.imageSource)
            explore_container.setBackgroundResource(items.itemBackground)

            setOnClickListener {
                onItemClickListener?.let {
                    it(items)
                }
                explore_container.startAnimation(logoAnim)
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Explore>() {
        override fun areItemsTheSame(oldItem: Explore, newItem: Explore): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Explore, newItem: Explore): Boolean {
            return oldItem == newItem
        }
    }
    private var onItemClickListener: ((Explore) -> Unit)? = null

    fun setOnItemClickListener(listener: (Explore) -> Unit) {
        onItemClickListener = listener
    }

    val differ = AsyncListDiffer(this, differCallBack)

    inner class ExploreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}