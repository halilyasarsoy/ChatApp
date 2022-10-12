package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateToRegister()
    }

    private fun navigateToRegister() {
        binding.buttonSignup.setOnClickListener {
            val actionToRegister =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterFragment()
            findNavController().navigate(actionToRegister)
        }
    }
}