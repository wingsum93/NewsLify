package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.entertainment_news.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EntertainmentNewsAdapter : RecyclerView.Adapter<EntertainmentNewsAdapter.EntertainmentNewsHolder>() {
    var showShimmer = true
    val SHIMMER_ITEM_NUMBER = 5
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
                entertainment_news_title.background=null
                entertainment_news_image.background=null
                entertainment_news_publishedAt.background=null
                entertainment_news_source.background=null

                if (article?.urlToImage.isNullOrEmpty()) {
                    entertainment_news_image.setBackgroundResource(R.color.colorPrimary)
                } else {
                    Picasso.get().load(article.urlToImage).fit().centerCrop()
                        .into(entertainment_news_image)
                }

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

                entertainment_news_publishedAt.text = formatted
                entertainment_news_title.text = article.title
                entertainment_news_source.text = article.source?.name

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