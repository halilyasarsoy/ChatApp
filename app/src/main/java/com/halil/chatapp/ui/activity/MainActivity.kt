package com.halil.chatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.halil.chatapp.R
import com.halil.chatapp.ui.fragment.LoginScreenFragment
import com.halil.chatapp.ui.fragment.SplashFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.BaseFragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}