package com.halil.chatapp.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentNotesBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
@AndroidEntryPoint
class NotesFragment : Fragment() {
    private val vml: MainViewModel by viewModels()
    private var selectedFileUri: Uri? = null

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!


    companion object {
        const val REQUEST_CODE_FILE = 1
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


    private fun generateRandomFileName(): String {
        return "file_${UUID.randomUUID()}"
    }

    private fun setAlertDialog() {
        binding.fabAdd.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog_notes, null)

            val universityEditText = dialogView.findViewById<EditText>(R.id.university_name)
            val departmentEditText = dialogView.findViewById<EditText>(R.id.department_name)
            val selectFileButton = dialogView.findViewById<Button>(R.id.select_file)

            selectFileButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(intent, REQUEST_CODE_FILE)
            }

            val builder = AlertDialog.Builder(requireContext())

            builder.setView(dialogView)
            builder.setPositiveButton("Tamam") { dialog, _ ->
                val university = universityEditText.text.toString()
                val department = departmentEditText.text.toString()
                vml.addNoteToFirestore(university, department)
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

    }
}
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
//            // Dosya seçici işlemi tamamlandı
//            // Seçilen dosyanın URI'sini al
//            selectedFileUri = data?.data
//        }
//    }
//}
//    private fun uploadFileToStorage(fileUri: Uri, onComplete: (String) -> Unit) {
//        val storageRef = FirebaseStorage.getInstance().reference
//
//        // Dosya adını belirleyin (örneğin, rastgele bir ID ile)
//        val fileName = generateRandomFileName()
//
//        // Depolama referansını oluşturun
//        val fileRef = storageRef.child("files/$fileName")
//
//        // Dosyayı depolama hizmetine yükleyin
//        val uploadTask = fileRef.putFile(fileUri)
//
//        // Yükleme işlemi tamamlandığında indirme bağlantısını elde edin
//        uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            fileRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUrl = task.result.toString()
//                onComplete(downloadUrl)
//            } else {
//
//            }
//        }
//    }