package com.crushtech.newslify.adapter

import android.app.Activity
import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.fragments.exploreFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import getTimeAgo
import kotlinx.android.synthetic.main.explore_bottom_items.view.*
import kotlinx.android.synthetic.main.explore_news_options_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class ExploreBottomAdapter(
    val exploreFragment: exploreFragment,
    val viewModel: NewsViewModel,
    val category: String,
    val activity: Activity
) : RecyclerView.Adapter<ExploreBottomAdapter.ExploreBottomViewHolder>() {
    private var btnCount = 0
    private var isClicked = false
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

            try {
                if (items?.urlToImage.isNullOrEmpty()) {
                    news_source_image.setBackgroundResource(R.color.colorPrimary)
                } else {
                    Picasso.get().load(items.urlToImage).fit().centerCrop()
                        .into(news_source_image)
                }
            } catch (e: Exception) {
            }

            val formatted = items.publishedAt
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                val pasTime = dateFormat.parse(formatted!!)
                val agoTime = getTimeAgo(pasTime!!)
                source_publishedAt.text = agoTime
            } catch (e: Exception) {

            }
            source_title.text = items.title
            source_des.text = items?.description
            items.category = category
            //get current theme
            val componentName = (activity as NewsActivity).componentName
            val currentTheme = activity.packageManager.getActivityInfo(
                componentName,
                0
            ).themeResource
            //pass theme to spinner
            val bottomDialog =
                BottomSheetDialog(context, currentTheme)
            val view =
                LayoutInflater.from(context).inflate(R.layout.explore_news_options_layout, null)
            bottomDialog.setContentView(view)
            select_more_options.setOnClickListener {
                bottomDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                bottomDialog.show()
            }
            view.saveExArticle.setOnClickListener {
                isClicked = true
                if (isClicked && btnCount == 0) {
                    viewModel.saveArticle(items)
                    val customSnackListener: View.OnClickListener = View.OnClickListener {
                        exploreFragment.findNavController().navigate(
                            R.id.action_exploreFragment_to_savedNewsFragment
                        )
                    }
                    SimpleCustomSnackbar.make(
                        this, "Article saved successfully", Snackbar.LENGTH_LONG,
                        customSnackListener, R.drawable.snack_fav,
                        "View", null
                    )?.show()
                    if (bottomDialog.isShowing) {
                        bottomDialog.hide()
                    }
                } else {
                    SimpleCustomSnackbar.make(
                        this, "Article saved already", Snackbar.LENGTH_LONG,
                        null, R.drawable.snack_fav,
                        "View", null
                    )?.show()
                    if (bottomDialog.isShowing) {
                        bottomDialog.hide()
                    }
                }
                btnCount++
            }
            view.copyExArticleLink.setOnClickListener {
                this.context.copyToClipboard(items.url.toString(), this.context)
                SimpleCustomSnackbar.make(
                    this, "Article Link Copied", Snackbar.LENGTH_LONG,
                    null, R.drawable.link_copied_icon,
                    "", null
                )?.show()
                if (bottomDialog.isShowing) {
                    bottomDialog.dismiss()
                }
            }
            view.shareExArticleLink.setOnClickListener {
                try {
                    val articleUrl = "From NewsLify:  ${items.url}"
                    val shareSub = "APP NAME/TITLE"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/link"
                        putExtra(Intent.EXTRA_SUBJECT, shareSub)
                        putExtra(Intent.EXTRA_TEXT, articleUrl)
                    }
                    if (bottomDialog.isShowing) {
                        bottomDialog.dismiss()
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Using"))
                } catch (e: ActivityNotFoundException) {
                }

            }

            setOnClickListener {
                onItemClickListener?.let {
                    it(items)
                }
            }
        }
    }

    private fun Context.copyToClipboard(text: CharSequence, context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("articleUrl", text)
        clipboard.setPrimaryClip(clip)
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