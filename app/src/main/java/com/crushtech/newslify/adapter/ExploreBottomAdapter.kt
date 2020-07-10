package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.ui.fragments.ExploreSource
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.explore_bottom_items.view.*
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExploreBottomAdapter : RecyclerView.Adapter<ExploreBottomAdapter.ExploreBottomViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExploreBottomAdapter.ExploreBottomViewHolder {
        return ExploreBottomViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.explore_bottom_items,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: ExploreBottomAdapter.ExploreBottomViewHolder,
        position: Int
    ) {
        holder.itemView.apply {
            val items = differ.currentList[position]

            if (items?.urlToImage.isNullOrEmpty()) {
                news_source_image.setBackgroundResource(R.color.colorPrimary)
            } else {
                Picasso.get().load(items.urlToImage).fit().centerCrop()
                    .into(news_source_image)
            }
            val formattedJsonDate = items.publishedAt?.substring(0, 10)
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

            source_publishedAt.text = "$formatted"
            source_title.text = items.title
            source_des.text = items?.description

//            val source=sourceList[position]
//            Picasso.get().load(source.img).fit().into(explore_source_img)
//            source_text.text = source.sourceName
//            source_motto.text = source.motto

            setOnClickListener {
                onItemClickListener?.let {
                    it(items)
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

    inner class ExploreBottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}