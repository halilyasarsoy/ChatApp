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
import com.halil.chatapp.R
import com.halil.chatapp.databinding.FragmentCommunicationBinding
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
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST)
        sendMessage()
    }

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