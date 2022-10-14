package com.halil.chatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentSettingBinding
import com.halil.chatapp.ui.activity.AuthActivity
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class SettingFragment : Fragment() {
    private var _binding : FragmentSettingBinding?=null
    private val binding get() = _binding!!
    private val mainViewModel : MainViewModel by viewModels ()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logout()
    }
    private fun logout() {
        binding.logoutbutton.setOnClickListener {
            mainViewModel.logout {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}