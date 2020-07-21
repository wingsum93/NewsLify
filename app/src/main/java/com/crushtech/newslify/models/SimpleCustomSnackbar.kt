package com.crushtech.newslify.models

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crushtech.newslify.R
import com.google.android.material.snackbar.BaseTransientBottomBar

class SimpleCustomSnackbar(
    parent: ViewGroup,
    content: SimpleCustomSnackbarView
) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {


    init {
        // getView().setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
        getView().setBackgroundResource(R.drawable.snack_bg)
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View,
            message: String, duration: Int,
            listener: View.OnClickListener?, icon: Int, action_label: String?, bg_color: Int?
        ): SimpleCustomSnackbar? {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.custom_view_inflation,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
                action_label?.let {
                    customView.tvAction.text = action_label
                    customView.tvAction.setOnClickListener {
                        listener?.onClick(customView.tvAction)
                    }
                }
                customView.imLeft.setImageResource(icon)
                if (bg_color != null) {
                    customView.layRoot.setBackgroundColor(bg_color)
                }


                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
                e.message?.let { Log.v("exception ", it) }
            }

            return null
        }

    }

}