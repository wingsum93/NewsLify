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
import kotlinx.android.synthetic.main.business_news_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class BusinessNewsCoverAdapter :
    RecyclerView.Adapter<BusinessNewsCoverAdapter.BusinessViewHolder>() {

    var showShimmer = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.business_news_item, parent, false)
        return BusinessViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: BusinessViewHolder,
        position: Int
    ) {

        holder.itemView.apply {
            if (showShimmer) {
                bs_shimmer.startShimmer()
            } else {
                val article = differ.currentList[position]
                bs_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                bnews_picture.background = null
                b_title.background = null
                b_publishedAt.background = null


                try {
                    if (article?.urlToImage.isNullOrEmpty()) {
                        bnews_picture.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        Picasso.get().load(article.urlToImage).fit().centerCrop()
                            .into(bnews_picture)
                    }
                } catch (e: Exception) {
                }

                val formatted = article.publishedAt
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                    val pasTime = dateFormat.parse(formatted!!)
                    val agoTime = getTimeAgo(pasTime!!)
                    b_publishedAt.text = agoTime
                } catch (e: Exception) {

                }
                b_title.text = article.title

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


    inner class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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