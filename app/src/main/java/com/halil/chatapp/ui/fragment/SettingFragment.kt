package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentSettingBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var imageURI: Uri
    private var imgName = ""
    private var imgUrlx: String? = null
    private val vml: MainViewModel by viewModels()


    private val uid = FirebaseAuth.getInstance().uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
        updateBtn()
        changeDepartment()

        val imageViewChange = binding.profileImages
        val docRef = uid?.let { db.collection("users").document(it) }
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    val mImageView = document.getString("imgUrl")
                    Glide.with(requireContext()).load(mImageView).into(imageViewChange)
                }
            }

        imageViewChange.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToFullScreenFragment()
            findNavController().navigate(action)
        }

    }

    private fun uploadImage() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        imgName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$imgName")
        storageReference.putFile(imageURI).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                imgUrlx = uri.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), "failure", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            imageURI = data.data!!
            binding.profileImages.setImageURI(imageURI)
            uploadImage()
        }
    }

    @SuppressLint("CheckResult")
    private fun updateBtn() {
        binding.changeProfileImage.setOnClickListener {
            if (imgUrlx?.isNotEmpty() == true) {
                val mapUpdate = mapOf("imgUrl" to imgUrlx)
                db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update(mapUpdate)
                Toast.makeText(
                    requireActivity(),
                    R.string.toastChangeImageSuccess,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireActivity(), R.string.toastChangeImageElse, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun update() {
        binding.editIcon.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun changeDepartment() {
        binding.changeDepartment.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog_notes, null)
            val universityEditText = dialogView.findViewById<EditText>(R.id.university_name)
            val departmentEditText = dialogView.findViewById<EditText>(R.id.department_name)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(dialogView)
            builder.setPositiveButton("Tamam") { dialog, _ ->
                val university = universityEditText.text.toString()
                val department = departmentEditText.text.toString()
                if (university.isEmpty() || department.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Lütfen tüm gerekli alanları doldurunuz ve dosya seçiniz.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    vml.addNoteToFirestore(university, department, requireContext())
                }
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}

