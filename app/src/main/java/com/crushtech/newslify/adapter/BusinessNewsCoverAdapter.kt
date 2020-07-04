package com.crushtech.newslify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cover_item.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BusinessNewsCoverAdapter :
    RecyclerView.Adapter<BusinessNewsCoverAdapter.BusinessViewHolder>() {

    var showShimmer = true
    val SHIMMER_ITEM_NUMBER = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cover_item, parent, false)
        return BusinessViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(showShimmer) SHIMMER_ITEM_NUMBER else differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: BusinessViewHolder,
        position: Int
    ) {

        holder.itemView.apply {
            if (showShimmer) {
                bs_shimmer.startShimmer()
            }else {
                val article = differ.currentList[position]
                bs_shimmer.apply {
                    stopShimmer()
                    setShimmer(null)
                }
                bnews_picture.background=null
                b_title.background=null
                b_publishedAt.background=null

                Picasso.get().load(article.urlToImage).fit().centerCrop()
                    .into(bnews_picture, object :
                        Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {
                            bnews_picture.setBackgroundResource(R.color.colorPrimary)
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

                b_publishedAt.text = formatted
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