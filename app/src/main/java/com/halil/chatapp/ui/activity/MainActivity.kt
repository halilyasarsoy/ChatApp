package com.halil.chatapp.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.databinding.ActivityMainBinding
import com.halil.chatapp.other.AppUtil
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private val appUtil = AppUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        getUID()?.let { vm.updateStatus(it, "online") }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        setupWithNavController(bottomNav, navController)

        val topDestinationIds = setOf(
            R.id.chatScreenFragment,
            R.id.detailUsersFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id !in topDestinationIds) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

    }
    fun getUID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Alert Dialog")
                builder.setMessage("çıkış yapmak üzeresiniz.")
                builder.setPositiveButton("yes") { _, _ ->

                    vm.logout {
                        getUID()?.let { vm.updateStatus(it, "offline") }
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                builder.setNegativeButton("no") { _, _ ->
                    Toast.makeText(this, "Çıkış yapmak için onaylayınız.", Toast.LENGTH_SHORT)
                        .show()
                }

                builder.create()
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
