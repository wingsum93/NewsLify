package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.ui.fragments.savedNewsFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_article_preview.view.*
import kotlinx.android.synthetic.main.saved_article_items.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SavedArticlesAdapter() :
    RecyclerView.Adapter<SavedArticlesAdapter.SavedArticlesViewHolder>() {
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

    override fun onBindViewHolder(
        holder: SavedArticlesAdapter.SavedArticlesViewHolder,
        position: Int
    ) {
        holder.itemView.apply {
            val article = differ.currentList[position]
            Picasso.get().load(article.urlToImage).fit().centerCrop()
                .into(saved_news_image, object :
                    Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception) {
                        saved_news_image.setBackgroundResource(R.color.shimmer_color)
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

            saved_news_publishedAt_and_source.text = "$formatted      ${article.source?.name}"
            saved_news_title.text = article?.title
            saved_news_description.text = article?.description
            category_tag.text = "Featured in ${article.category}"

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