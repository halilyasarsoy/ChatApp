package com.halil.chatapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val auth = FirebaseAuth.getInstance()
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
       application.setTheme(R.style.DarkTheme)
        setContentView(R.layout.activity_auth)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.authFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        if (auth.currentUser != null) {
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}