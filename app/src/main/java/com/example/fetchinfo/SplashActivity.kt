package com.example.fetchinfo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val viewImage = findViewById<ImageView>(R.id.imageSplash)

        //for removing
        //For removing  status bar
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //adding animation
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        viewImage.animation = splashAnimation

        splashAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                //delay of 2000ms
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    },1000
                )
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })
    }
}