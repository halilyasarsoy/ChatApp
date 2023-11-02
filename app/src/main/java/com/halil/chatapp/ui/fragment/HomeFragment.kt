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
import com.google.firebase.database.FirebaseDatabase
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FragmentHomeBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.activity.MainActivity
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("User-Status")

    private val vm: MainViewModel by viewModels()
    private val listAdapter = ListAdapter(arrayListOf())
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getUser()
        adapterSetup()
        checkUserList()
    }

    private fun adapterSetup() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = listAdapter
        }

        listAdapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(user: Users) {


                val direction = user.imgUrl?.let {
                    HomeFragmentDirections.actionHomeFragmentToChatScreenFragment(
                        user.name,
                        user.lastname,
                        it,
                        user.uid,
                        user.profession
                    )
                }
                if (direction != null) {
                    findNavController().navigate(direction)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Bu kullanıcı henüz onaylanmadı!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }

    private fun checkUserList() {
        vm.userList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Missing or incorrect login information",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Loading -> {
                    binding.progressBarUserList.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarUserList.visibility = View.GONE
                    val userList = it.data as? ArrayList<Users>
                    userList?.let { newList ->
                        listAdapter.updateUserStatus(newList)
                    }
                }

                else -> {}
            }
        }
    }

}


