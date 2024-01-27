package com.halil.chatapp.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.method.TextKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentRegisterBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()
    private lateinit var imageURI: Uri
    private var imgUrlx: String? = null
    private var imgName = ""
    private lateinit var progressBar: ProgressBar
    var selectedUniversity: String? = null // Başlangıçta null
    private var passwordVisible = false
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
        registerSuccess(it = TextKeyListener.Capitalize.CHARACTERS)
        selectImage()
        progressBar = binding.progressBarRegister
        val universityArray = resources.getStringArray(R.array.university_names)
        val universityEditText = binding.tvProfession
        val universityDropdownImageView = binding.universityDropdown

        val universityAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            universityArray
        )

        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.tvProfession.setOnClickListener {
            showUniversityPopup(universityArray, universityEditText)
        }

        universityDropdownImageView.setOnClickListener {
            showUniversityPopup(universityArray, universityEditText)
        }
        passwordToggle()
        confirmPasswordToggle()
        universityEditText.keyListener = null

    }

    fun showUniversityPopup(universityArray: Array<String>, targetEditText: EditText) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Üniversite Seç")
        builder.setItems(universityArray) { dialog, which ->
            selectedUniversity = universityArray[which]
            targetEditText.setText(selectedUniversity)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun registerSuccess(it: TextKeyListener.Capitalize): TextKeyListener.Capitalize {

        binding.registerButtonAuth.setOnClickListener {
            if (imgUrlx?.isNotEmpty() == true) {
                val test = imgUrlx
                val name = binding.registerNameText.text.toString().trim()
                val lastname = binding.registerLastName.text.toString().trim()
                val email = binding.registerEmailtext.text.toString().trim()
                val password = binding.registerPasswordtext.text.toString().trim()
                val confirmPassword = binding.registerConfirmpasswordText.text.toString().trim()

                if (selectedUniversity == null) {
                    Toast.makeText(context, "Lütfen üniversite seçin", Toast.LENGTH_SHORT).show()
                } else {
                    test?.let { its ->
                        vm.register(
                            name,
                            lastname,
                            email,
                            password,
                            confirmPassword,
                            selectedUniversity!!, // Seçilen üniversiteyi direkt kullanın
                            its
                        )
                    }
                }
            } else {
                Toast.makeText(context, "fillblank", Toast.LENGTH_LONG).show()
            }

        }
        return it
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
        if (imageURI.toString().isNotEmpty()) {
            binding.descChooseProfileImage.visibility = View.GONE
        } else {
            binding.descChooseProfileImage.visibility = View.VISIBLE
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

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Kayıt işlemi başarılı! Geri dönmek için tıklayın.")
                    builder.setPositiveButton(getString(R.string.backToLogin)) { dialog, which ->
                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToLoginScreenFragment()
                        findNavController().navigate(action)
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                else -> {}
            }
        }
    }

    private fun passwordToggle() {
        binding.passwordVisibilityToggle.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                binding.registerPasswordtext.inputType =
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordVisibilityToggle.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                binding.registerPasswordtext.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordVisibilityToggle.setImageResource(R.drawable.baseline_visibility_24)
            }

            binding.registerPasswordtext.text?.let { it1 ->
                binding.registerPasswordtext.setSelection(
                    it1.length
                )
            }
        }
    }

    private fun confirmPasswordToggle() {
        binding.confirmPasswordVisibilityToggle.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                binding.registerConfirmpasswordText.inputType =
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.confirmPasswordVisibilityToggle.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                binding.registerConfirmpasswordText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.confirmPasswordVisibilityToggle.setImageResource(R.drawable.baseline_visibility_24)
            }

            binding.registerConfirmpasswordText.text?.let { it1 ->
                binding.registerConfirmpasswordText.setSelection(
                    it1.length
                )
            }
        }
    }

    private fun navigateToLogin() {
        binding.buttonTurnLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginScreenFragment()
            findNavController().navigate(action)
        }
    }
}



