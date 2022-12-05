package com.halil.chatapp.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.halil.chatapp.databinding.FragmentRegisterBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()
    private lateinit var imageURI: Uri
    private lateinit var imgUrlx: String
    private var imgName = ""
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCheck()
        navigateToLogin()
        registerSuccess()
        selectImage()

        progressBar = binding.progressBarRegister
    }

    private fun navigateToLogin() {
        binding.buttonTurnLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginScreenFragment()
            findNavController().navigate(action)
        }
    }

    private fun registerSuccess() {
        binding.registerButtonAuth.setOnClickListener {
            val test = imgUrlx
            val name = binding.registerNameText.text.toString().trim()
            val lastname = binding.registerLastName.text.toString().trim()
            val email = binding.registerEmailtext.text.toString().trim()
            val password = binding.registerPasswordtext.text.toString().trim()
            val confirmpassword = binding.registerConfirmpasswordText.text.toString().trim()
            vm.register(name, lastname, email, password, confirmpassword, test)
        }
    }

    private fun registerCheck() {
        vm.registerStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Please fill all blanks", Toast.LENGTH_LONG)
                        .show()
                }
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToRegisterSuccessFragment()
                    findNavController().navigate(action)
                }
                else -> {}
            }
        }
    }

    private fun uploadImage() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        imgName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$imgName")
        storageReference.putFile(imageURI).addOnSuccessListener {
            downloadImage()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            imageURI = data.data!!
            binding.imageView.setImageURI(imageURI)
            uploadImage()
        }
    }

    private fun selectImage() {
        binding.imageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun downloadImage() {
        FirebaseStorage.getInstance().reference.child("images")
            .child(imgName).downloadUrl.addOnSuccessListener { uri ->
                imgUrlx = uri.toString()
            }
    }
}



