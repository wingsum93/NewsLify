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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.entertainment_news.view.*
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BreakingNewsAdapter : RecyclerView.Adapter<BreakingNewsAdapter.BreakingNewsHolder>() {

    var showShimmer = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakingNewsHolder {
        return BreakingNewsHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
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

                if (article?.urlToImage.isNullOrEmpty()) {
                    breaking_news_image.setBackgroundResource(R.color.colorPrimary)
                } else {
                    Picasso.get().load(article.urlToImage).fit().centerCrop()
                        .into(breaking_news_image)
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    publishedAt_and_source.text = "$agoTime      ${article.source?.name}"
                } catch (e: Exception) {

                }
                title.text = article?.title
                description.text = article?.description

                setOnClickListener {
                    onItemClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
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