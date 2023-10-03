package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.halil.chatapp.adapter.NotesAdapter
import com.halil.chatapp.databinding.FragmentNotesListBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListFragment : Fragment() {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by viewModels()
    private var notesListAdapter = NotesAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val universityName = arguments?.getString("university")
        val department = arguments?.getString("department")
        if (universityName != null) {
            if (department != null) {
                vm.getNotesList(universityName, department)
            }
        }
        notesAdapterSetup()
        checkDepartmentList()
    }

    private fun notesAdapterSetup() {
        binding.notesListRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            notesListAdapter = NotesAdapter(arrayListOf())
            adapter = notesListAdapter
        }
    }

    private fun checkDepartmentList() {
        vm.notesList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Missing or incorrect login information",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {
                    binding.progressBarNotesList.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBarNotesList.visibility = View.GONE

                    val departmentList = resource.data as? ArrayList<String>
                    departmentList?.let { ArrayList(it) }
                        ?.let {
                            Log.e("URLs :::", departmentList.toString())
                            notesListAdapter.setDataChange(it)
                        }
                }
            }
        }
    }
}