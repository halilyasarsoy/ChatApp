package com.halil.chatapp.ui.activity

import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.FirebaseDatabase
import com.halil.chatapp.R
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.adapter.PageAdapter
import com.halil.chatapp.data.User
import com.halil.chatapp.databinding.ActivityMainBinding

import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding : ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2
//    var database : FirebaseDatabase?=null
//    var users : ArrayList<User>?=null
//    var usersAdapter: ListAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tablayoutSet ()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> vm.logout {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun tablayoutSet (){
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.adapter = PageAdapter(this)
        TabLayoutMediator(tabLayout,viewPager){tab,index->
            tab.text = when(index){
                0-> {"Home"}
                1->{"Settings"}
                else->{throw NotFoundException("notFound")}
            }
        }.attach()

    }
}
