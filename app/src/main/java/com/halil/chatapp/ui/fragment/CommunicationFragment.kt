package com.halil.chatapp.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentCommunicationBinding
import com.halil.chatapp.other.MyDataObject.dortYillikBolumler
import com.halil.chatapp.other.MyDataObject.dortYillikBolumlerEN
import com.halil.chatapp.other.MyDataObject.esitdortYillikBolumler
import com.halil.chatapp.other.MyDataObject.esitdortYillikBolumlerEN
import com.halil.chatapp.other.MyDataObject.esitikiYilliBolumler
import com.halil.chatapp.other.MyDataObject.esitikiYilliBolumlerEN
import com.halil.chatapp.other.MyDataObject.ikiYilliBolumler
import com.halil.chatapp.other.MyDataObject.ikiYilliBolumlerEN
import com.halil.chatapp.other.MyDataObject.sayisaldortYillikBolumler
import com.halil.chatapp.other.MyDataObject.sayisaldortYillikBolumlerEN
import com.halil.chatapp.other.MyDataObject.sayisalikiYilliBolumler
import com.halil.chatapp.other.MyDataObject.sayisalikiYilliBolumlerEN
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CommunicationFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentCommunicationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        pushDataToFirestore()
//        pushDataToFirestoreEN()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST)
        sendMessage()
    }


//    fun pushDataToFirestore() {
//        val db = FirebaseFirestore.getInstance()
//        val collectionReference = db.collection("department")
//        val documentReference = collectionReference.document("department-tr")
//
//        val data = HashMap<String, Any>()
//
//        // Sözel Bölümler ana başlık altında 4 Yıllık ve 2 Yıllık Bölümler
//        val sozelBolumler = HashMap<String, Any>()
//        sozelBolumler["4 Yıllık Bölümler"] = dortYillikBolumler
//        sozelBolumler["2 Yıllık Bölümler"] = ikiYilliBolumler
//        data["Sözel Bölümler"] = sozelBolumler
//
//        val sayisalBolumler = HashMap<String, Any>()
//        sayisalBolumler["4 Yıllık Bölümler"] = sayisaldortYillikBolumler
//        sayisalBolumler["2 Yıllık Bölümler"] = sayisalikiYilliBolumler
//        data["Sayısal Bölümler"] = sayisalBolumler
//
//        val esitAgirlik = HashMap<String, Any>()
//        esitAgirlik["4 Yıllık Bölümler"] = esitdortYillikBolumler
//        esitAgirlik["2 Yıllık Bölümler"] = esitikiYilliBolumler
//        data["Eşit Ağırlık Bölümler"] = esitAgirlik
//
////        val departmentData = mapOf("department" to data) // "department" anahtarını bir harita olarak saklayın
//
//        documentReference.set(data)
//            .addOnSuccessListener {
//                println("Veri başarıyla Firestore'a eklendi.")
//            }
//            .addOnFailureListener { e ->
//                println("Firestore'a veri eklenirken bir hata oluştu: $e")
//            }
//    }
//
//    fun pushDataToFirestoreEN() {
//        val db = FirebaseFirestore.getInstance()
//        val collectionReference = db.collection("department")
//        val documentReference = collectionReference.document("department-en")
//
//        val data = HashMap<String, Any>()
//
//        val sozelBolumler = HashMap<String, Any>()
//        sozelBolumler["4 Yıllık Bölümler"] = dortYillikBolumlerEN
//        sozelBolumler["2 Yıllık Bölümler"] = ikiYilliBolumlerEN
//        data["Sözel Bölümler"] = sozelBolumler
//
//        val sayisalBolumler = HashMap<String, Any>()
//        sayisalBolumler["4 Yıllık Bölümler"] = sayisaldortYillikBolumlerEN
//        sayisalBolumler["2 Yıllık Bölümler"] = sayisalikiYilliBolumlerEN
//        data["Sayısal Bölümler"] = sayisalBolumler
//
//        val esitAgirlik = HashMap<String, Any>()
//        esitAgirlik["4 Yıllık Bölümler"] = esitdortYillikBolumlerEN
//        esitAgirlik["2 Yıllık Bölümler"] = esitikiYilliBolumlerEN
//        data["Eşit Ağırlık Bölümler"] = esitAgirlik
//
//        documentReference.set(data)
//            .addOnSuccessListener {
//                println("Veri başarıyla Firestore'a eklendi.")
//            }
//            .addOnFailureListener { e ->
//                println("Firestore'a veri eklenirken bir hata oluştu: $e")
//            }
//    }

    private fun sendMessage() {
        binding.btnSubmit.setOnClickListener {
            val mDialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_alert_dialog_message_sent, null)
            val msg = binding.etxtMessage.text.toString().trim()
            val myLdt =
                Calendar.getInstance().time.toString() + Calendar.getInstance().timeZone.displayName

            activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setTitle(getString(R.string.message_confirm))
                    setMessage(getString(R.string.message_confirm_message))
                    setPositiveButton(
                        R.string.yesAnswer
                    ) { dialog, _ ->
                        if (msg.isNotEmpty()) {
                            viewModel.sendMessages(msg, myLdt)
                            MaterialAlertDialogBuilder(requireContext(), 2)
                                .setTitle(getString(R.string.message_sent_title))
                                .setMessage(getString(R.string.message_sent_msg))
                                .setView(mDialogView)
                                .setPositiveButton("OK") { dialog, which ->
                                    binding.etxtMessage.text.clear()
                                }.show()
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.message_empty_error),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    setNegativeButton(
                        R.string.cancel
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                }
                builder.create()
                builder.show()
            }
        }
    }
}