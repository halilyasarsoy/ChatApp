package com.halil.chatapp.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    companion object {
        const val REQUEST_CODE_FILE = 1
    }
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
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
        binding.fabAdd.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.alert_dialog_notes, null)

            builder.setView(view)
            builder.setPositiveButton("Tamam") { dialog, _ ->
                // Tamam düğmesine basıldığında yapılacak işlemler
                dialog.dismiss() // Diyalogu kapat
            }
            builder.setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss() // Diyalogu kapat
            }

            val selectFileButton = view.findViewById<Button>(R.id.select_file)
            selectFileButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, REQUEST_CODE_FILE)
            }

            val dialog = builder.create()
            dialog.show()
        }

    }

}