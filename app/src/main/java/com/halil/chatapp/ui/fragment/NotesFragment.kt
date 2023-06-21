package com.halil.chatapp.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentNotesBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private val vml: MainViewModel by viewModels()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private var selectedFileUri: Uri? = null
    private var fileUrl: String = ""

    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAlertDialog()
    }


    private fun setAlertDialog() {
        binding.fabAdd.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog_pick_file, null)
            val selectFileButton = dialogView.findViewById<Button>(R.id.select_file_notes)
            selectFileButton.setOnClickListener {
                openFilePicker()
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setPositiveButton("Tamam") { dialog, _ ->
                val fileUri = selectedFileUri
                if (fileUri == null) {
                    Toast.makeText(
                        requireContext(),
                        "Lütfen tüm gerekli alanları doldurunuz ve dosya seçiniz.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    uploadFile(fileUri)
                }
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun uploadFile(fileUri: Uri) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val imgName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$imgName")

        storageReference.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    fileUrl = uri.toString()
                    addFileUrlToFirestore()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Dosya yüklenirken hata oluştu: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun addFileUrlToFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            val notesRef = FirebaseFirestore.getInstance()
                .collection("notes")
                .document(userEmail)

            val fieldUpdate = hashMapOf<String, Any>(
                "userUrls" to FieldValue.arrayUnion(fileUrl)
            )

            notesRef.set(fieldUpdate, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Dosya URL'si veritabanına eklendi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Dosya URL'si veritabanına eklenirken hata oluştu: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            selectedFileUri = fileUri
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
