package com.crushtech.newslify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.crushtech.newslify.ui.util.Constants.Companion.STREAK

class StreakReset : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.getSharedPreferences(STREAK, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
        p0?.getSharedPreferences("TIME", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }
}