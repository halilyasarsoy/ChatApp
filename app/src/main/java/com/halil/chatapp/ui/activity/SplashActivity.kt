package com.halil.chatapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halil.chatapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val scope = MainScope()
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startDelay()
    }
    private fun startDelay() {
        stopDelay()
        job = scope.launch {
            delay(1500)
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