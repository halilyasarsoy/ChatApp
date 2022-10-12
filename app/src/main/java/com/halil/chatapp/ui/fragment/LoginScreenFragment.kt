package com.halil.chatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentLoginScreenBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.activity.AuthActivity

@AndroidEntryPoint
class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private val vm : MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateToRegister()
        loginCheck()
        login()
    }

    private fun navigateToRegister() {
        binding.buttonSignup.setOnClickListener {
            val actionToRegister =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterFragment()
            findNavController().navigate(actionToRegister)
        }
    }
    private fun login (){
        binding.buttonLogin.setOnClickListener {
            val mail = binding.emailText.text.toString().trim()
            val password = binding.passwordText.text.toString().trim()
            vm.login(email = mail , password = password)
        }
    }

    private fun loginCheck(){
        vm.loginStatus.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Error -> {
                    //Toast Err
                }
                is Resource.Loading -> {
                    //Progressbar visible
                }
                is Resource.Success -> {
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                else -> {}
            }
        }
    }



}