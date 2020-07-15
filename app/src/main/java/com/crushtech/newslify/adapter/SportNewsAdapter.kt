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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.item_article_preview.view.*
import kotlinx.android.synthetic.main.science_list_item.view.*
import kotlinx.android.synthetic.main.sport_list_item.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SportNewsAdapter : RecyclerView.Adapter<SportNewsAdapter.SportNewsViewHolder>() {

    var showShimmer = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportNewsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_list_item, parent, false)
        return SportNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportNewsViewHolder, position: Int) {

        holder.itemView.apply {
            if (showShimmer) {
                sp_shimmer.startShimmer()
            } else {
                val article = differ.currentList[position]
                sp_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                sport_news_picture.background = null
                sport_title.background = null
                sport_publishedAt.background = null

                if (article?.urlToImage.isNullOrEmpty()) {
                    sport_news_picture.setBackgroundResource(R.color.colorPrimary)
                } else {
                    Picasso.get().load(article.urlToImage).fit().centerCrop()
                        .into(sport_news_picture)
                }
                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    sport_publishedAt.text = agoTime
                } catch (e: Exception) {

                }

                sport_title.text = article.title


                setOnClickListener {
                    onItemClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }


    inner class SportNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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