package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentRegisterSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class RegisterSuccessFragment : Fragment() {
    private var _binding: FragmentRegisterSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        binding.backtoLogin.setOnClickListener {
            val action =
                RegisterSuccessFragmentDirections.actionRegisterSuccessFragmentToLoginScreenFragment()
            findNavController().navigate(action)
        }
    }

}