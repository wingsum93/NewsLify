package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.ui.util.Constants
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.science_list_item.view.*
import kotlinx.android.synthetic.main.search_news_items.view.*
import java.text.SimpleDateFormat
import java.util.*

class SearchNewsAdapter : RecyclerView.Adapter<SearchNewsAdapter.SearchNewsAdapterViewHolder>() {
    var showShimmer = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNewsAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_news_items, parent, false)
        return SearchNewsAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (showShimmer) Constants.SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchNewsAdapterViewHolder, position: Int) {
        holder.itemView.apply {
            if (showShimmer) {
                search_news_shimmer.startShimmer()
            } else {
                val article = differ.currentList[position]
                search_news_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                search_news_picture.background = null
                search_news_title.background = null
                search_news_desc.background = null
                search_news_publishedAt.background = null

                try {
                    if (article?.urlToImage.isNullOrEmpty()) {
                        search_news_picture.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        Picasso.get().load(article.urlToImage).fit().centerCrop()
                            .into(search_news_picture)
                    }
                } catch (e: Exception) {
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    search_news_publishedAt.text = agoTime
                } catch (e: Exception) {

                }

                search_news_title.text = article.title
                search_news_desc.text = article.description
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


    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    inner class SearchNewsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}