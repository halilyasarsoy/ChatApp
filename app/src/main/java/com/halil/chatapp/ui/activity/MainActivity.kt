package com.halil.chatapp.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.databinding.ActivityMainBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.halil.chatapp.other.Resource
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var headerView: View
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var profileImageView: CircleImageView
    private val db = FirebaseFirestore.getInstance()
    private val mRef = FirebaseDatabase.getInstance().reference
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm.getUserData()
        drawerLayout = findViewById(R.id.drawerLayout)
        setupNavigationMenuListener()
        updateNavHeader()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.chatScreenFragment,
                R.id.detailUsersFragment,
                R.id.fullScreenFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomAppBar.visibility = View.VISIBLE

                }
            }
            appBarConfiguration = AppBarConfiguration(
                navController.graph, binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            binding.navDrawView.setupWithNavController(navController)
            binding.bottomNavigationView.background = null
            binding.bottomNavigationView.setupWithNavController(navController)

            getUID()?.let { vm.updateStatus(it, "online") }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    fun getUID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
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
            R.id.deleteAccount ->{

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigationMenuListener() {
        binding.navDrawView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteAccount -> {
                    Toast.makeText(this, "Delete Account tıklandı!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainFragmentContainerView)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun updateNavHeader() {
        val navHeaderView = binding.navDrawView.getHeaderView(0)
        headerView = navHeaderView
        profileImageView = headerView.findViewById(R.id.profileImageView)
        val navUsername = navHeaderView.findViewById<TextView>(R.id.navHeaderName)
        val navUserLastName = navHeaderView.findViewById<TextView>(R.id.navHeaderLastName)
        val navUniversity = navHeaderView.findViewById<TextView>(R.id.navHeaderProfession)

        vm.userData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Glide.with(this).load(it.data?.imgUrl).into(profileImageView)
                    navUsername.text = it.data?.name
                    navUserLastName.text = it.data?.lastname
                    navUniversity.text = it.data?.profession
                }

                else -> {}
            }
        }
    }
}
