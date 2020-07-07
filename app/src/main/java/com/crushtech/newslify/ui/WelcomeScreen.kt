package com.crushtech.newslify.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.crushtech.newslify.R
import kotlinx.android.synthetic.main.activity_welcome_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_welcome_screen)
        val logoAnim: Animation = AnimationUtils.loadAnimation(
           this,
           R.anim.welcome_transition
       )
        welcome_logo.startAnimation(logoAnim)
        app_text.animation=logoAnim
        val intent= Intent(this,NewsActivity::class.java)
        GlobalScope.launch(Dispatchers.Main) {
            delay(5000)
            startActivity(intent)
            overridePendingTransition(R.anim.button_anim,R.anim.slide_out_left)
            finish()
        }






    }
}