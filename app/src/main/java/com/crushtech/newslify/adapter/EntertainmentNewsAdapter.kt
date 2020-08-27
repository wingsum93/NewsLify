package com.crushtech.newslify.adapter

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
import kotlinx.android.synthetic.main.entertainment_news.view.*
import java.text.SimpleDateFormat
import java.util.*

class EntertainmentNewsAdapter : RecyclerView.Adapter<EntertainmentNewsAdapter.EntertainmentNewsHolder>() {
    var showShimmer = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntertainmentNewsHolder {
        return EntertainmentNewsHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.entertainment_news,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    override fun onBindViewHolder(holder: EntertainmentNewsHolder, position: Int) {

        holder.itemView.apply {
            if(showShimmer){
                et_shimmer.startShimmer()
            }
            else {
                val article = differ.currentList[position]
                et_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                entertainment_news_title.background = null
                entertainment_news_image.background = null
                entertainment_news_publishedAt.background = null
                entertainment_news_source.background = null

                try {
                    if (article?.urlToImage.isNullOrEmpty()) {
                        entertainment_news_image.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        Picasso.get().load(article.urlToImage).fit().centerCrop()
                            .into(entertainment_news_image)
                    }
                } catch (e: Exception) {
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    entertainment_news_publishedAt.text = agoTime
                } catch (e: Exception) {

                }

                entertainment_news_title.text = article.title
                entertainment_news_source.text = article.source?.name

                entertainment_news_image.apply {
                    transitionName = article.title
                }
                setOnClickListener {
                    onItemClickListener?.invoke(entertainment_news_image, article)
                }
            }
        }
    }

    private var onItemClickListener: ((transitionView: View, article: Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (View, Article) -> Unit) {
        onItemClickListener = listener

    }

    inner class EntertainmentNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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