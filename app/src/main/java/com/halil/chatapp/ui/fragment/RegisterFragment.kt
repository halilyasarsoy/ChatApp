package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateToLogin()
        navigateToSuccess()
    }

    private fun navigateToLogin() {
        binding.buttonTurnLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginScreenFragment()
            findNavController().navigate(action)
        }
    }
    private fun navigateToSuccess(){
        binding.registerButtonSignin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToRegisterSuccessFragment()
            findNavController().navigate(action)
        }
    }


}