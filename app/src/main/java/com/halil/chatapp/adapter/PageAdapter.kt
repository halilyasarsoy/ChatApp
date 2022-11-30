package com.halil.chatapp.adapter

import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.halil.chatapp.ui.fragment.HomeFragment
import com.halil.chatapp.ui.fragment.SettingFragment

class PageAdapter (fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> {HomeFragment()}
            1 -> {SettingFragment()}
            else -> {throw NotFoundException("not found")}
        }
    }
}