package com.halil.chatapp.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.ui.activity.AuthActivity


class DeleteBottomSheetFragment : BottomSheetDialogFragment() {
    private val db = FirebaseFirestore.getInstance()
    private val mRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delete_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            val confirmDialog = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.deleteAccounts)
                .setMessage(R.string.lastAlert)
                .setPositiveButton(R.string.yesAnswer) { _, _ ->
                    mRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .removeValue()
                        .addOnSuccessListener {
                            FirebaseAuth.getInstance().currentUser!!.delete()
                                .addOnCompleteListener {
                                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                                    startActivity(intent)

                                    Toast.makeText(
                                        requireActivity(),
                                        R.string.toastSuccess,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    db.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
                        .addOnFailureListener {
                            Toast.makeText(
                                requireActivity(),
                                "Account not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    dismiss()
                }
                .setNegativeButton(R.string.noAnswer) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            confirmDialog.show()
        }

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }

    }
}



