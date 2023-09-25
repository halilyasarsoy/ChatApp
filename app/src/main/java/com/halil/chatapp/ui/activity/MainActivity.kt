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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.databinding.ActivityMainBinding
import com.halil.chatapp.ui.fragment.NotesFragment
import com.halil.chatapp.ui.fragment.NotesFragmentDirections
import com.halil.chatapp.ui.fragment.SettingFragment
import com.halil.chatapp.ui.fragment.SettingFragmentDirections
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var isDrawerOpen = false
    private lateinit var headerView: View
    private lateinit var profileImageView: CircleImageView
    private val db = FirebaseFirestore.getInstance()
    private val mRef = FirebaseDatabase.getInstance().reference

    private val topDestinationIds = setOf(
        R.id.chatScreenFragment,
        R.id.detailUsersFragment,
        R.id.fullScreenFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navDrawView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        getUID()?.let { vm.updateStatus(it, "online") }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        setupWithNavController(bottomNav, navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id !in topDestinationIds) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }
        binding.navDrawView.setupWithNavController(navController)
        setupNavigationMenuListener()
        headerView = navView.getHeaderView(0)
        profileImageView = headerView.findViewById(R.id.profileImageView)
        val nameTextView: TextView = headerView.findViewById(R.id.navHeaderName)
        val lastNameTextView: TextView = headerView.findViewById(R.id.navHeaderLastName)
        val professionTextView: TextView = headerView.findViewById(R.id.navHeaderProfession)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val docRef = uid?.let { db.collection("users").document(it) }
        docRef?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val mImageView = snapshot.getString("imgUrl")
                val name = snapshot.getString("name")
                val lastName = snapshot.getString("lastname")
                val profession = snapshot.getString("profession")
                Glide.with(this).load(mImageView).into(profileImageView)
                nameTextView.text = name
                lastNameTextView.text = lastName
                professionTextView.text = profession
            }
        }
    }
    private fun setupNavigationMenuListener() {
        val navigationView: NavigationView = findViewById(R.id.navDrawView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteAccount -> {
                    val bottomSheetDialog = BottomSheetDialog(this)
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_delete_account, null)
                    bottomSheetDialog.setContentView(view)
                    val deleteButton: Button = view.findViewById(R.id.deleteButton)
                    val cancelButton: Button = view.findViewById(R.id.cancelButton)
                    deleteButton.setOnClickListener {
                        val confirmDialog = AlertDialog.Builder(this)
                            .setTitle(R.string.deleteAccounts)
                            .setMessage(R.string.lastAlert)
                            .setPositiveButton(R.string.yesAnswer) { _, _ ->
                                mRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .removeValue()
                                    .addOnSuccessListener {
                                        FirebaseAuth.getInstance().currentUser!!.delete()
                                            .addOnCompleteListener {
                                                val intent = Intent(this, AuthActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                                Toast.makeText(
                                                    this,
                                                    R.string.toastSuccess,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                db.collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            "Account not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                bottomSheetDialog.dismiss() // Close the bottom sheet
                            }
                            .setNegativeButton(R.string.noAnswer) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                        confirmDialog.show()
                    }
                    cancelButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.behavior.state =
                        BottomSheetBehavior.STATE_EXPANDED // slowed open bottom sheet
                    bottomSheetDialog.show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.notesFragment -> {
                    loadNotesFragment()
                    drawerLayout.closeDrawer(GravityCompat.START)// close menu
                    true
                }
                R.id.settingFragment -> {
                    loadSettingFragment()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadNotesFragment() {
        val fragment =
            NotesFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainerView, fragment)
            .commit()
    }

    private fun loadSettingFragment() {
        val fragment =
            SettingFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainerView, fragment)
            .commit()
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
            R.id.settingFragment -> {
                loadSettingFragment()

            }
            android.R.id.home -> {
                if (isDrawerOpen) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    isDrawerOpen = false
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                    isDrawerOpen = true
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
