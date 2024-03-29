package com.halil.chatapp.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.databinding.ActivityMainBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var headerView: View
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var profileImageView: CircleImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomAppBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm.getUserData()
        drawerLayout = findViewById(R.id.drawerLayout)
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
        bottomNavigationView = findViewById(R.id.bottomAppBar)

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
                builder.setTitle(getString(R.string.logOut))
                builder.setMessage(getString(R.string.questionLogOut))
                builder.setPositiveButton(getString(R.string.yesAnswer)) { _, _ ->
                    vm.logout {
                        getUID()?.let { vm.updateStatus(it, "offline") }
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                builder.setNegativeButton(getString(R.string.noAnswer)) { _, _ ->
                    Toast.makeText(this, getString(R.string.confirmLogOut), Toast.LENGTH_SHORT)
                        .show()
                }

                builder.create()
                builder.show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainFragmentContainerView)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun updateNavHeader() {
        val navHeaderView = binding.navDrawView.getHeaderView(0)
        val profileImageView = navHeaderView.findViewById<ImageView>(R.id.profileImageView)
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

    fun hideBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
    }
}
