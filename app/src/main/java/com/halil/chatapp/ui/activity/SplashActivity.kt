package com.halil.chatapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.halil.chatapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint

class SplashActivity : AppCompatActivity() {
    private val scope = MainScope()
    private var job: Job? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val lineView = findViewById<View>(R.id.lineView)
        val kLogoAnim = findViewById<ImageView>(R.id.kLogoAnim)
        val kWordAnim = findViewById<ImageView>(R.id.kWordAnim)


        val lineAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -5f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        lineAnimation.duration = 1000

// logoAnimation'ın tanımı (sağdan sola)
        val logoAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 2f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        logoAnimation.duration = 1000

// wordAnimation'ın tanımı (soldan sağa)
        val wordAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -2f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        wordAnimation.duration = 1000

// Animasyonların başlangıçta görünmez olması
        kLogoAnim.visibility = View.INVISIBLE
        kWordAnim.visibility = View.INVISIBLE

        lineView.startAnimation(lineAnimation)

        lineAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Animasyonlar lineAnimation bittikten sonra başlasın
                kLogoAnim.visibility = View.VISIBLE
                kWordAnim.visibility = View.VISIBLE
                kLogoAnim.startAnimation(logoAnimation)
                kWordAnim.startAnimation(wordAnimation)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        startDelay()
    }

//    private fun viewAnimation() {
//        val lineView = findViewById<View>(R.id.lineView)
//        val animation = TranslateAnimation(
//            Animation.RELATIVE_TO_SELF, 0f,
//            Animation.RELATIVE_TO_SELF, 0f,
//            Animation.RELATIVE_TO_SELF, -5f,
//            Animation.RELATIVE_TO_SELF, 0f
//        )
//        animation.duration = 1000
//        lineView.startAnimation(animation)
//    }
//    private fun kWordsAnimation(){
//        val kWordKey = findViewById<ImageView>(R.id.kWordKey)
//
//
//    }

    private fun startDelay() {
        stopDelay()
        job = scope.launch {
            delay(2700)
            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun stopDelay() {
        job?.cancel()
        job = null
    }
}