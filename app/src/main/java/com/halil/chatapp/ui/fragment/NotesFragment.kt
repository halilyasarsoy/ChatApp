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
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.data.UserStorage
import com.halil.chatapp.databinding.FragmentNotesBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private val vml: MainViewModel by viewModels()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private var selectedFileUri: Uri? = null


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
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog_notes, null)

            val universityEditText = dialogView.findViewById<EditText>(R.id.university_name)
            val departmentEditText = dialogView.findViewById<EditText>(R.id.department_name)
            val selectFileButton = dialogView.findViewById<Button>(R.id.select_file)

            selectFileButton.setOnClickListener {
                openFilePicker()
            }

            val builder = AlertDialog.Builder(requireContext())

            builder.setView(dialogView)
            builder.setPositiveButton("Tamam") { dialog, _ ->
                val university = universityEditText.text.toString()
                val department = departmentEditText.text.toString()
                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email
                val fileUri = selectedFileUri
                val userStorage = email?.let { UserStorage(it) }

                if (university.isEmpty() || department.isEmpty() || fileUri == null) {
                    Toast.makeText(requireContext(), "Lütfen tüm gerekli alanları doldurunuz ve dosya seçiniz.", Toast.LENGTH_SHORT).show()
                } else {
                    if (userStorage != null) {
                        vml.uploadFile(userStorage, fileUri, {
                            Toast.makeText(requireContext(), "Dosya yüklendi", Toast.LENGTH_SHORT).show()
                        }, { exception ->
                            Toast.makeText(requireContext(), "Dosya yüklenirken hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                        })
                    }

                    vml.addNoteToFirestore(university, department)
                }

                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { fileUri ->
                    selectedFileUri = fileUri  // Seçilen dosyanın URI'sini sakla
                }
            }
        }
}
