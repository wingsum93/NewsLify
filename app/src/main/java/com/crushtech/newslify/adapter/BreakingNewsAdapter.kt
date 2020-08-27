package com.crushtech.newslify.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.ui.util.Constants.Companion.SHIMMER_ITEM_NUMBER
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.breaking_news_items.view.*
import java.text.SimpleDateFormat
import java.util.*

class BreakingNewsAdapter : RecyclerView.Adapter<BreakingNewsAdapter.BreakingNewsHolder>() {

    var showShimmer = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakingNewsHolder {
        return BreakingNewsHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.breaking_news_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BreakingNewsHolder, position: Int) {
        holder.itemView.apply {
            if (showShimmer) {
                brk_shimmer.startShimmer()
            } else {
                val article = differ.currentList[position]
                brk_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                breaking_news_image.background = null
                publishedAt_and_source.background = null
                title.background = null
                description.background = null

                try {
                    if (article?.urlToImage.isNullOrEmpty()) {
                        breaking_news_image.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        Picasso.get().load(article.urlToImage).fit().centerCrop()
                            .into(breaking_news_image)
                    }
                } catch (e: Exception) {
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    publishedAt_and_source.text = "$agoTime      ${article.source?.name}"
                } catch (e: Exception) {

                }
                title.text = article?.title
                description.text = article?.description
                breaking_news_image.apply {
                    transitionName = article.title
                }
                setOnClickListener {
                    onItemClickListener?.invoke(breaking_news_image, article)
                }
            }
        }
    }

    private var onItemClickListener: ((transitionView: View, article: Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (View, Article) -> Unit) {
        onItemClickListener = listener

    }


    inner class BreakingNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)


}