package com.halil.chatapp.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.halil.chatapp.databinding.FragmentLoginScreenBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.activity.MainActivity
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        loadLocale()
        navigateToRegister()
        loginCheck()
        login()
//        binding.changeLang.setOnClickListener {
//            changeLang()
//        }

    }
//
//    private fun changeLang() {
//            val listItems = arrayOf("en", "tr")
//            val mBuilder = AlertDialog.Builder(requireContext())
//            mBuilder.setTitle("Choose Language")
//            mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
//                if (which == 0) {
//                    setLocale("en")
//                    activity?.recreate()
//                } else if (which == 1) {
//                    setLocale("tr")
//                    activity?.recreate()
//                }
//                dialog.dismiss()
//            }
//            val mDialog = mBuilder.create()
//            mDialog.show()
//    }
//
//    private fun setLocale(Lang: String) {
//        val locale = Locale(Lang)
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.locale = locale
//        activity?.baseContext?.resources?.updateConfiguration(
//            config,
//            activity?.baseContext?.resources?.displayMetrics
//        )
//        val editor = activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)?.edit()
//        editor?.putString("My_Lang", Lang)
//    }
//
//    private fun loadLocale() {
//        val sharedPref = activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
//        val language = sharedPref?.getString("My_Lang", "")
//            setLocale(language!!)
//
//    }

    private fun navigateToRegister() {
        binding.buttonSignupAuth.setOnClickListener {
            val actionToRegister =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterFragment()
            findNavController().navigate(actionToRegister)
        }
    }

    private fun login() {
        binding.buttonLogin.setOnClickListener {
            val mail = binding.emailText.text.toString().trim()
            val password = binding.passwordText.text.toString().trim()
            vm.login(email = mail, password = password)
        }
    }

    private fun loginCheck() {
        vm.loginStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Missing or incorrect login information",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {
                    val progressBar = binding.progressBar3
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                else -> {}
            }
        }
    }
}

