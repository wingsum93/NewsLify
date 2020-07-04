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


class BusinessNewsCoverAdapter :
    RecyclerView.Adapter<BusinessNewsCoverAdapter.BusinessViewHolder>() {

    var businessShimmerEffect: ShimmerFrameLayout?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cover_item, parent, false)
        return BusinessViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BusinessViewHolder,
        position: Int
    ) {
        val article = differ.currentList[position]
        holder.itemView.apply {

            Picasso.get().load(article.urlToImage).fit().centerCrop()
                .into(bnews_picture, object :
                    Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception) {
                        bnews_picture.setBackgroundResource(R.drawable.ic_launcher_background)
                    }
                })
            val formattedJsonDate = article.publishedAt?.substring(0, 9)
           // val dateformat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            b_publishedAt.text= formattedJsonDate
            b_title.text = article.title

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