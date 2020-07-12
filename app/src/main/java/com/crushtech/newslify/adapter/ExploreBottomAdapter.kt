package com.crushtech.newslify.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.crushtech.newslify.ui.fragments.exploreFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoastlibrary.StyleableToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.explore_bottom_items.view.*
import kotlinx.android.synthetic.main.explore_news_options_layout.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ExploreBottomAdapter(
    val exploreFragment: exploreFragment,
    val viewModel: NewsViewModel,
    val category: String
) : RecyclerView.Adapter<ExploreBottomAdapter.ExploreBottomViewHolder>() {
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

            source_publishedAt.text = formatted
            source_title.text = items.title
            source_des.text = items?.description
            items.category = category

            val bottomDialog =
                BottomSheetDialog(context, R.style.Theme_MaterialComponents_BottomSheetDialog)
            val view =
                LayoutInflater.from(context).inflate(R.layout.explore_news_options_layout, null)
            bottomDialog.setContentView(view)
            select_more_options.setOnClickListener {
                bottomDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                bottomDialog.show()
            }
            view.saveExArticle.setOnClickListener {
                viewModel.saveArticle(items)
                val customSnackListener: View.OnClickListener = View.OnClickListener {
                    exploreFragment.findNavController().navigate(
                        R.id.action_exploreFragment_to_savedNewsFragment
                    )
                }
                SimpleCustomSnackbar.make(
                    this, "Article saved successfully", Snackbar.LENGTH_LONG,
                    customSnackListener, R.drawable.snack_fav,
                    "View", ContextCompat.getColor(context, R.color.mygrey)
                )?.show()
                if (bottomDialog.isShowing) {
                    bottomDialog.hide()
                }
            }
            view.copyExArticleLink.setOnClickListener {
                this.context.copyToClipboard(items.url.toString(), this.context)
                SimpleCustomSnackbar.make(
                    this, "Article Link Copied", Snackbar.LENGTH_LONG,
                    null, R.drawable.link_copied_icon,
                    "", ContextCompat.getColor(context, R.color.mygrey)
                )?.show()
                if (bottomDialog.isShowing) {
                    bottomDialog.hide()
                }
            }
//            view.shareExArticleLink.setOnClickListener {
//                StyleableToast.makeText(context, "shared", R.style.customToast).show()
//            }

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