package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
import com.halil.chatapp.ui.viewmodel.MainViewModel_Factory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var imageURI: Uri
    private var imgName = ""
    private var imgUrlx: String? = null
    private val viewModel: MainViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null
    private var editIconVisible = false


    private val uid = FirebaseAuth.getInstance().uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

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
                progressDialog?.dismiss()
                // Edit icon'un görünür hale gelmesi
                editIconVisible = true
                binding.editIcon.visibility = View.VISIBLE

            }
        }.addOnFailureListener {
            progressDialog?.dismiss()

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
        binding.editIcon.visibility = View.GONE

        binding.editIcon.setOnClickListener {
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

    private fun startProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Bekleyin...")
        progressDialog?.show()
    }

    private fun update() {
        binding.changeProfileImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
            startProgressDialog()
        }
    }

    private fun changeDepartment() {
        val departmentEditText = binding.departmentEditText
        binding.changeDepartmentName.setOnClickListener {
            showDepartmentSelectionDialog(departmentEditText)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showDepartmentSelectionDialog(editText: EditText) {
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog_notes, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val departmentListView = dialogView.findViewById<ListView>(R.id.departmentListView)

        val departmentList = resources.getStringArray(R.array.departmentall_names)

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, departmentList)
        departmentListView.adapter = adapter
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                adapter.filter.filter(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s)
            }
        })

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(dialogView)

        val dialog = builder.create()

        departmentListView.setOnItemClickListener { _, _, position, _ ->
            val selectedDepartment = departmentList[position]
            editText.setText(selectedDepartment)

            // Kullanıcı bir seçenek seçtikten sonra işlemi otomatikleştir
            val department = selectedDepartment
            if (department.isNotEmpty()) {
                viewModel.addNoteToFirestore(department, requireContext())
                viewModel.department.observe(viewLifecycleOwner) { department ->

                }

            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.bind()
    }
}

