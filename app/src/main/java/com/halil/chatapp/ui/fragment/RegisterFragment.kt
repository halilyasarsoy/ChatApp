package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.halil.chatapp.databinding.FragmentRegisterBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerCheck()
        navigateToLogin()
        registerSuccess()
    }

    private fun navigateToLogin() {
        binding.buttonTurnLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginScreenFragment()
            findNavController().navigate(action)
        }
    }

    private fun registerSuccess() {
        binding.registerButtonAuth.setOnClickListener {
            val name = binding.registerNameText.text.toString().trim()
            val lastname = binding.registerLastName.text.toString().trim()
            val email = binding.registerEmailtext.text.toString().trim()
            val password = binding.registerPasswordtext.text.toString().trim()
            val confirmpassword = binding.registerConfirmpasswordText.text.toString().trim()
            vm.register(name, lastname, email, password, confirmpassword)
        }
    }

    private fun registerCheck() {
        vm.registerStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                Toast.makeText(requireContext(),"Please fill all blanks",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                //progress bar
                }
                is Resource.Success -> {
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToRegisterSuccessFragment()
                    findNavController().navigate(action)
                }
                else -> {}
            }
        }
    }
}