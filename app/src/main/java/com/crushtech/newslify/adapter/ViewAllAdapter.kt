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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_article_preview.view.*
import kotlinx.android.synthetic.main.view_all_news_items.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ViewAllAdapter : RecyclerView.Adapter<ViewAllAdapter.VAdapter>() {
    var showShimmer = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllAdapter.VAdapter {
        return VAdapter(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_all_news_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (showShimmer) Constants.SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewAllAdapter.VAdapter, position: Int) {
        holder.itemView.apply {
            if (showShimmer) {
                view_all_shimmer.startShimmer()
            } else {
                val article = differ.currentList[position]
                view_all_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                view_all_news_image.background = null
                view_all_news_publishedAt_and_source.background = null
                view_all_news_title.background = null
                view_all_news_description.background = null
                Picasso.get().load(article.urlToImage).fit().centerCrop()
                    .into(view_all_news_image, object :
                        Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {
                            view_all_news_image.setBackgroundResource(R.color.shimmer_color)
                        }
                    })
                val formattedJsonDate = article.publishedAt?.substring(0, 10)
                val dateformat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                var date: Date? = null
                try {
                    date = dateformat.parse(formattedJsonDate!!)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val calendar = Calendar.getInstance()
                calendar.time = date!!
                val formatted = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)

                view_all_news_publishedAt_and_source.text =
                    "$formatted      ${article.source?.name}"
                view_all_news_title.text = article?.title
                view_all_news_description.text = article?.description

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

    inner class VAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)
}