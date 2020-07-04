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
import kotlinx.android.synthetic.main.science_list_item.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScienceNewsAdapter : RecyclerView.Adapter<ScienceNewsAdapter.ScienceNewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScienceNewsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.science_list_item, parent, false)
        return ScienceNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScienceNewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Picasso.get().load(article.urlToImage).fit().centerCrop()
                .into(science_news_picture, object :
                    Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception) {
                       science_news_picture.setBackgroundResource(R.color.colorPrimary)
                    }
                })
            val formattedJsonDate = article.publishedAt?.substring(0, 9)

            science_title.text = article.title
            science_publishedAt.text = formattedJsonDate

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
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