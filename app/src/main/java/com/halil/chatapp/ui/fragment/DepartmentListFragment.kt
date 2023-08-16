package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.halil.chatapp.adapter.DepartmentAdapter
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.databinding.FragmentDepartmentListBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DepartmentListFragment : Fragment() {

    private var _binding: FragmentDepartmentListBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by viewModels()
    private var departmentAdapter = DepartmentAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepartmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val universityName = arguments?.getString("university")

        if (universityName != null) {
            vm.getDepartmentList(universityName)
        }
        departmentAdapterSetup()
        checkDepartmentList()
    }

    private fun departmentAdapterSetup() {
        binding.departmentListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            departmentAdapter = DepartmentAdapter(arrayListOf())
            adapter = departmentAdapter

            departmentAdapter.setOnItemClickListener(object :
                DepartmentAdapter.OnItemClickListener {
                override fun onItemClick(university: GetListUniversityNotes) {
                    val action =
                        NotesFragmentDirections.actionNotesFragmentToDepartmentListFragment(
                            university.university
                        )
                    findNavController().navigate(action)
                }
            })
        }
    }

    private fun checkDepartmentList() {
        vm.universityNameList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Missing or incorrect login information",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {
                    // Yükleme durumu
                }
                is Resource.Success -> {
                    val departmentList = resource.data as? ArrayList<GetListUniversityNotes>
                    departmentList?.let { ArrayList(it) }
                        ?.let { departmentAdapter.setDataChange(it) }
                }
            }
        }
    }

}