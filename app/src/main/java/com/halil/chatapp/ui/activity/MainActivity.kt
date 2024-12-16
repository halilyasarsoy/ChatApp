package com.halil.chatapp.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.adapter.FriendRequestAdapter
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ActivityMainBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.fragment.HomeFragment
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomAppBar
    private var listAdapter = ListAdapter(arrayListOf())

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
            vm.fetchUserStatuses()

            getUID()?.let { vm.updateStatus(it, "online") }
        }
        bottomNavigationView = findViewById(R.id.bottomAppBar)
        // Kullanıcı durumunu onDisconnect ile yönet
        getUID()?.let { uid ->
            vm.updateStatusWithDisconnect(uid, "online")
        }
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            vm.fetchApprovedFriends()
            vm.fetchFriendRequests(currentUserId)
        }
        binding.navDrawView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_users -> {
                    vm.getUser()
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_list, null)

                    val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

                    checkUserList(progressBar)
                    showUserListDialog()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }


    fun showUserListDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_list, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter

        listAdapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(user: Users) {
            }

            override fun onFriendRequestClick(user: Users) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    vm.sendFriendRequest(currentUserId, user.uid)
                    listAdapter.updateItem(user.uid)
                    Toast.makeText(
                        this@MainActivity,
                        "${user.name} kullanıcısına arkadaşlık isteği gönderildi!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Gönderilen isteği listeye ekle
                    val updatedSentRequests = listAdapter.sentRequests.toMutableList()
                    updatedSentRequests.add(user.uid)
                    listAdapter.updateSentRequests(updatedSentRequests)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Arkadaşlık isteği gönderilirken bir hata oluştu!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        val dialog = AlertDialog.Builder(this)
            .setTitle("All Users")
            .setView(dialogView)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()

        checkUserList(progressBar)
    }

    private fun checkUserList(progressBar: ProgressBar) {
        vm.userList.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    val userList = resource.data as? ArrayList<Users>
                    userList?.let { newList ->
                        listAdapter.updateUserStatus(newList)
                    }
                }

                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Kullanıcılar yüklenemedi", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Uygulama tamamen kapandığında offline güncelle
        getUID()?.let { vm.updateStatus(it, "offline") }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        val menuItem = menu?.findItem(R.id.friendRequests)
        val actionView = menuItem?.actionView

        val badge = actionView?.findViewById<TextView>(R.id.badge)

        vm.friendRequestCount.observe(this) { count ->
            if (count > 0) {
                badge?.visibility = View.VISIBLE
                badge?.text = count.toString()
            } else {
                badge?.visibility = View.GONE
            }
        }

        actionView?.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        return super.onCreateOptionsMenu(menu)
    }


    private fun getUID(): String? {
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

            R.id.friendRequests -> {
                showFriendRequestsDialog() // Arkadaşlık isteklerini göster
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFriendRequestsDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_friend_requests, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FriendRequestAdapter(
            arrayListOf(),
            onApprove = { user ->
                vm.approveFriendRequest(user)
            },
            onReject = { userId ->
                vm.rejectFriendRequest(userId)
            }
        )
        recyclerView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Friend Requests")
            .setView(dialogView)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()

        vm.friendRequests.observe(this) { requests ->
            Log.d("Activity", "Friend requests size: ${requests.size}")

            progressBar.visibility = View.GONE
            adapter.updateRequests(requests)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainFragmentContainerView)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun updateNavHeader() {
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
