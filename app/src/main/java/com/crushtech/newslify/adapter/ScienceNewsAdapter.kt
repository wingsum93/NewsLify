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
import kotlinx.android.synthetic.main.science_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ScienceNewsAdapter : RecyclerView.Adapter<ScienceNewsAdapter.ScienceNewsViewHolder>() {

    var showShimmer = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScienceNewsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.science_list_item, parent, false)
        return ScienceNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScienceNewsViewHolder, position: Int) {

        holder.itemView.apply {
            if(showShimmer){
                sc_shimmer.startShimmer()
            }else {
                val article = differ.currentList[position]
                sc_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                science_news_picture.background = null
                science_title.background = null
                science_publishedAt.background = null

                try {
                    if (article?.urlToImage.isNullOrEmpty()) {
                        science_news_picture.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        Picasso.get().load(article.urlToImage).fit().centerCrop()
                            .into(science_news_picture)
                    }
                } catch (e: Exception) {
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    science_publishedAt.text = agoTime
                } catch (e: Exception) {

                }

                science_title.text = article.title

                science_news_picture.apply {
                    transitionName = article.title
                }
                setOnClickListener {
                    onItemClickListener?.invoke(science_news_picture, article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }


    private var onItemClickListener: ((transitionView: View, article: Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (View, Article) -> Unit) {
        onItemClickListener = listener

    }


    inner class ScienceNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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