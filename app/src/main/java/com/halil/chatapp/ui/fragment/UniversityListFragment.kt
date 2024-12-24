package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.halil.chatapp.R
import com.halil.chatapp.adapter.UniversityAdapter
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.databinding.FragmentNotesBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UniversityListFragment : Fragment() {

    private val vml: MainViewModel by viewModels()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private var selectedFileUri: Uri? = null
    private var fileUrl: String = ""
    private var universityNamesAdapter = UniversityAdapter(arrayListOf())
    private lateinit var headerView: View
    private lateinit var searchView: SearchView
    private lateinit var editText: EditText
    private var fileType = ""

    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAlertDialog()
        vml.getUniversityName()
        universityNameAdapterSet()
        checkUniversityNameList()
        approvedUser()
        editText = view.findViewById(R.id.editTextSearch)
        searchViewCreated()
        headerView = requireActivity().findViewById(R.id.navDrawView)
        change()

    }

    private fun searchViewCreated() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vml.searchUniversity(p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vml.searchUniversity(p0.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                vml.searchUniversity(s.toString())
            }
        })
    }

    private fun checkUniversityNameList() {
        vml.universityNameList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    // Hata durumu: Boş liste
                    Toast.makeText(
                        requireContext(), "Universities not found", Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Success -> {
                    val universityNameList = resource.data
                    updateRecyclerView(universityNameList ?: emptyList())
                    binding.fabAddProgressBar.visibility = View.GONE
                }

                is Resource.Loading -> {
                    binding.fabAddProgressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
    }

    private fun updateRecyclerView(universityNameList: List<GetListUniversityNotes>) {
        universityNamesAdapter.setDataChange(ArrayList(universityNameList))
    }


    private fun approvedUser() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if (userEmail != null) {
            val sanitizedEmail = userEmail.replace(".", "_dot_")
            val databaseReference = FirebaseDatabase.getInstance().getReference("approved-user")
            val userReference = databaseReference.child(sanitizedEmail).child("approved")
            val sharedPreferences =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        userReference.setValue(false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Veritabanı hatası: ${databaseError.message}")
                }
            })

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val isApproved = dataSnapshot.getValue(Boolean::class.java) ?: false

                    if (isApproved) {
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isApproved", true)
                        editor.apply()
                        binding.fabAddProgressBar.visibility = View.GONE
                        binding.fabAdd.visibility = View.VISIBLE
                    } else {
                        binding.fabAddProgressBar.visibility = View.GONE
                        binding.fabAdd.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Veritabanı hatası: ${databaseError.message}")
                }
            })
        }
    }

    private fun change() {
        vml.fetchNotesData(requireContext())
        vml.universityData.observe(viewLifecycleOwner) { department ->
            val denemetext: TextView = headerView.findViewById(R.id.departmentNameHeaderView)
            denemetext.text = department.toString()
        }
    }

    private fun universityNameAdapterSet() {
        binding.getUniversityNamesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            universityNamesAdapter = UniversityAdapter(arrayListOf())
            adapter = universityNamesAdapter

            universityNamesAdapter.setOnItemClickListener(object :
                UniversityAdapter.OnItemClickListener {
                override fun onItemClick(university: GetListUniversityNotes) {
                    val action =
                        UniversityListFragmentDirections.actionUniversityListFragmentToDepartmentListFragment(
                            university.university
                        )
                    findNavController().navigate(action)
                }
            })
        }
    }

    private fun setAlertDialog() {
        binding.fabAdd.setOnClickListener {
            vml.fetchNotesData(requireContext())
            vml.universityData.observe(viewLifecycleOwner) { department ->

                val dialogView = layoutInflater.inflate(R.layout.alert_dialog_pick_file, null)
                val selectFileButton = dialogView.findViewById<Button>(R.id.select_file_notes)
                val okButton = dialogView.findViewById<Button>(R.id.positiveButton)
                val cancelButton = dialogView.findViewById<Button>(R.id.negativeButton)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(dialogView)

                val dialog = builder.create()

                selectFileButton.setOnClickListener {
                    openFilePicker()
                }
                okButton.setOnClickListener {
                    dialog.dismiss()
                    val fileUri = selectedFileUri
                    if (fileUri != null) {
                        uploadFile(fileUri)
                    } else {
                        Toast.makeText(
                            requireContext(), R.string.pickFile, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun uploadFile(fileUri: Uri) {


        val user = FirebaseAuth.getInstance().currentUser
        val currentTimeMillis = System.currentTimeMillis()
        val formattedDateTime =
            SimpleDateFormat("dd.MM.yyyy : HH:mm", Locale.getDefault()).format(currentTimeMillis)
        val storageReference =
            FirebaseStorage.getInstance().getReference("${user?.email} - $formattedDateTime")
        storageReference.putFile(fileUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                fileUrl = uri.toString() + fileType
                addFileUrlToFirestore()

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                requireContext(), R.string.fileError, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addFileUrlToFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        if (uid != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            usersCollection.document(uid).get().addOnSuccessListener { documentSnapshot ->
                val profession = documentSnapshot.get("profession") as? String
                if (profession != null) {
                    val notesRef =
                        FirebaseFirestore.getInstance().collection("notes").document(profession)
                    val departmentRef =
                        FirebaseFirestore.getInstance().collection("university").document(uid)
                    departmentRef.get().addOnSuccessListener { departmentDocument ->
                        val department = departmentDocument.getString("department")
                        if (department != null) {
                            val fieldUpdate = hashMapOf<String, Any>(
                                department to FieldValue.arrayUnion(fileUrl)
                            )
                            notesRef.set(fieldUpdate, SetOptions.merge()).addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(), R.string.fileUploaded, Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(), R.string.fileError, Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }.addOnFailureListener { exception ->
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
            if (data?.data.toString().contains("image")) {
                fileType = ".img"
            } else {
                fileType = ".pdf"
            }
            selectedFileUri = fileUri
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
