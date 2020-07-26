package com.crushtech.newslify.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.fragments.ThemeItems
import com.crushtech.newslify.ui.fragments.settingsFragment
import com.crushtech.newslify.ui.util.Constants
import kotlinx.android.synthetic.main.custom_themes_items.view.*

class PremiumThemesAdapter : RecyclerView.Adapter<PremiumThemesAdapter.ThemesViewHolder>() {
    private var selectedSwitchPosition: Int = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PremiumThemesAdapter.ThemesViewHolder {
        return ThemesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_themes_items, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PremiumThemesAdapter.ThemesViewHolder, position: Int) {
        val themeItems = differ.currentList[position]
        holder.itemView.apply {
            premiumThemeText.text = themeItems.themeName
            lottie_theme.setAnimation(themeItems.lottieRaw)
            val sharedprefs: SharedPreferences =
                context.getSharedPreferences("switchState", Context.MODE_PRIVATE)
            val isChecked = sharedprefs.getInt("switchPos", selectedSwitchPosition)
            themeIsActivated.isChecked = isChecked == position
            themeBackground.setBackgroundResource(themeItems.themeBackground)


            themeIsActivated.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val editor = holder.itemView.context.getSharedPreferences(
                        "switchState",
                        Context.MODE_PRIVATE
                    )
                    selectedSwitchPosition = holder.adapterPosition
                    themeIsActivated.isChecked = true
                    editor.edit().putInt("switchPos", selectedSwitchPosition).apply()

                    val intent = Intent(context, NewsActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ThemeItems>() {
        override fun areItemsTheSame(oldItem: ThemeItems, newItem: ThemeItems): Boolean {
            return oldItem.themeName == newItem.themeName
        }

        override fun areContentsTheSame(oldItem: ThemeItems, newItem: ThemeItems): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    inner class ThemesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}