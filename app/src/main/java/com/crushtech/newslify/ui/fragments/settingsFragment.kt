package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.renderscript.RenderScript
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK
import kotlinx.android.synthetic.main.settings_layout.*

class settingsFragment:Fragment(R.layout.settings_layout){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences(STREAK, Context.MODE_PRIVATE)
        val streakCount = prefs.getInt(STREAK, 0)
        dailynewsGoals.text = "Goals reached: $streakCount news articles read today"
    }



}