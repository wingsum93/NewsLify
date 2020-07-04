package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BreakingNewsAdapter : RecyclerView.Adapter<BreakingNewsAdapter.BreakingNewsHolder>() {

    var showShimmer = true
    val SHIMMER_ITEM_NUMBER = 5
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
                Picasso.get().load(article.urlToImage).fit().centerCrop()
                    .into(breaking_news_image, object :
                        Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {
                            breaking_news_image.setBackgroundResource(R.color.brown)
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

                publishedAt_and_source.text = "$formatted      ${article.source?.name}"
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