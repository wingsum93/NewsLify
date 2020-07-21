package com.crushtech.newslify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.crushtech.newslify.R
import com.crushtech.newslify.models.ScreenItems
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.saved_article_items.view.*

class IntroViewPagerAdapter(val context: Context, val screenItems: List<ScreenItems>) :
    PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return screenItems.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as View))
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(
            container.context
        ).inflate(R.layout.intro_layout_screen, container, false)
        val title = view.findViewById<TextView>(R.id.select_country)
        val introImage = view.findViewById<LottieAnimationView>(R.id.intro_img)
        val introDesc = view.findViewById<TextView>(R.id.intro_description)
        val logoimg = view.findViewById<ImageView>(R.id.logo_img)

        val currentItem = screenItems[position]

        title.let {
            title.text = currentItem.Title
        }
        introDesc.let {
            introDesc.text = currentItem.Description
        }
        introImage.let {
            if(currentItem.Title=="Welcome to Newslify") {
                Picasso.get().load(R.drawable.logo_a).fit().centerCrop()
                    .into(logoimg)
                introImage.setBackgroundResource(android.R.color.transparent)
            }
            introImage.apply {
                currentItem.ScreenImg?.let {
                        it1 -> setAnimation(it1)
                }
                playAnimation()
            }
        }
        container.addView(view)

        return view
    }
}