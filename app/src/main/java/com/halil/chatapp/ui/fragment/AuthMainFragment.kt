package com.halil.chatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentAuthMainBinding
import com.halil.chatapp.ui.activity.MainActivity
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthMainFragment : Fragment() {
    private val vm: MainViewModel by viewModels()
    private val authvm: AuthViewModel by viewModels()
    private var _binding: FragmentAuthMainBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance().signOut()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logout()
    }

    fun logout() {
        binding.logoutbutton.setOnClickListener {
            authvm.logout {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}
