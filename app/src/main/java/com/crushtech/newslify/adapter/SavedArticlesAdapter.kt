package com.crushtech.newslify.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.saved_article_items.view.*
import java.text.SimpleDateFormat
import java.util.*


class SavedArticlesAdapter :
    RecyclerView.Adapter<SavedArticlesAdapter.SavedArticlesViewHolder>() {
    var streakCount = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedArticlesAdapter.SavedArticlesViewHolder {
        return SavedArticlesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.saved_article_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: SavedArticlesAdapter.SavedArticlesViewHolder,
        position: Int
    ) {
        holder.itemView.apply {
            val article = differ.currentList[position]
            try {
                if (article?.urlToImage.isNullOrEmpty()) {
                    saved_news_image.setBackgroundResource(R.color.colorPrimary)
                } else {
                    Picasso.get().load(article.urlToImage).fit().centerCrop()
                        .into(saved_news_image)
                }
            } catch (e: Exception) {
            }

            val formatted = article.publishedAt
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                val pasTime = dateFormat.parse(formatted!!)
                val agoTime = getTimeAgo(pasTime!!)
                saved_news_publishedAt_and_source.text = "$agoTime      ${article.source?.name}"

            } catch (e: Exception) {

            }

            saved_news_title.text = article?.title
            saved_news_description.text = article?.description
            category_tag.text = "featured in ${article?.category} "

            val articleTime = article.timeInsertedToRoomDatabase
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
                val pasTime = dateFormat.parse(articleTime!!)
                val agoTime = getTimeAgo(pasTime!!)
                article?.savedTime = agoTime
                timeInserted.text = "saved: ${article?.savedTime}"

            } catch (e: Exception) {

            }


            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
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

    inner class SavedArticlesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}