package com.halil.chatapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.adapter.ApprovedUsersAdapter
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FragmentHomeBinding
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val vm: MainViewModel by viewModels()
    private val listAdapter = ListAdapter(arrayListOf())
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var approvedUsersAdapter: ApprovedUsersAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            vm.fetchSentFriendRequests(currentUserId)
        }
        vm.getUser()
        observeViewModel()
        observeFriendRequestStatus()

        setupApprovedUsersRecyclerView()
        vm.fetchUserStatuses()
        vm.fetchApprovedFriends()
    }

    private fun observeViewModel() {
        vm.sentFriendRequests.observe(viewLifecycleOwner) { sentList ->
            listAdapter.updateSentRequests(sentList)
        }

        vm.approvedUsers.observe(viewLifecycleOwner) { approvedList ->
            listAdapter.updateApprovedUsers(approvedList)
            approvedUsersAdapter.updateApprovedUsers(approvedList)
        }

        vm.friendRequestStatus.observe(viewLifecycleOwner) { statusMessage ->
            statusMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                vm.resetFriendRequestStatus()
            }
        }
    }

    private fun setupApprovedUsersRecyclerView() {
        approvedUsersAdapter = ApprovedUsersAdapter(arrayListOf())
        vm.userStatuses.observe(viewLifecycleOwner) { statuses ->
            Log.d("Fragment", "User Statuses: $statuses")
            approvedUsersAdapter.updateUserStatuses(statuses)
        }
        approvedUsersAdapter.setOnItemClickListener(object :
            ApprovedUsersAdapter.OnItemClickListener {
            override fun onItemClick(user: Users) {
                val action = HomeFragmentDirections.actionHomeFragmentToChatScreenFragment(
                    userId = user.uid,
                    name = user.name,
                    lastname = user.lastname,
                    imgUrl = user.imgUrl ?: "",
                    university = user.profession
                )
                findNavController().navigate(action)
            }


        })
        binding.recyclerViewApprovedUsers.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = approvedUsersAdapter
        }
        observeViewModel()
    }

    private fun observeFriendRequestStatus() {
        vm.friendRequestStatus.observe(viewLifecycleOwner) { statusMessage ->
            statusMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}