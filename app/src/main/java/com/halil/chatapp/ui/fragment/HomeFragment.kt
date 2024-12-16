package com.halil.chatapp.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.R
import com.halil.chatapp.adapter.ApprovedUsersAdapter
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FragmentHomeBinding
import com.halil.chatapp.other.Resource
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
            vm.fetchSentFriendRequests(currentUserId) // Arkadaşlık isteklerini al
        }
        vm.getUser()
        checkUserList()
        observeViewModel()

        observeFriendRequestStatus()
        binding.btnShowAllUsers.setOnClickListener {
            showUserListDialog() // Pop-up dialog'u aç
        }
        setupApprovedUsersRecyclerView() // RecyclerView ayarları

        vm.fetchApprovedFriends()
    }

    private fun observeViewModel() {
        // Gönderilen istekleri gözlemle
        vm.sentFriendRequests.observe(viewLifecycleOwner) { sentList ->
            listAdapter.updateSentRequests(sentList)
        }

        // Onaylanan arkadaşları gözlemle
        vm.approvedUsers.observe(viewLifecycleOwner) { approvedList ->
            listAdapter.updateApprovedUsers(approvedList)
            approvedUsersAdapter.updateApprovedUsers(approvedList)
        }

        // Arkadaşlık isteği durumunu gözlemle
        vm.friendRequestStatus.observe(viewLifecycleOwner) { statusMessage ->
            statusMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                vm.resetFriendRequestStatus() // Mesajı sıfırla
            }
        }
    }

    private fun setupApprovedUsersRecyclerView() {
        approvedUsersAdapter = ApprovedUsersAdapter(arrayListOf())

        approvedUsersAdapter.setOnItemClickListener(object : ApprovedUsersAdapter.OnItemClickListener {
            override fun onItemClick(user: User) {
                // ChatScreenFragment'a yönlendirme
                val action = HomeFragmentDirections.actionHomeFragmentToChatScreenFragment(
                    userId = user.uid,
                    name = user.name,
                    lastname = user.lastname,  // Eksik parametre eklendi
                    imgUrl = user.imgUrl ?: "",
                    university = user.profession
                )
                findNavController().navigate(action)
            }


        })
        binding.recyclerViewApprovedUsers.apply { // recyclerViewApprovedUsers, layout içinde tanımlanmalı
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = approvedUsersAdapter
        }
    }

    private fun showUserListDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_list, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        // RecyclerView'i ayarla
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listAdapter

        listAdapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(user: Users) {
                // Bu dialogda onItemClick çalışmayacak, boş bırakabiliriz
            }

            override fun onFriendRequestClick(user: Users) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    vm.sendFriendRequest(currentUserId, user.uid)
                    listAdapter.updateItem(user.uid) // Anlık güncelleme
                    Toast.makeText(
                        requireContext(),
                        "${user.name} kullanıcısına arkadaşlık isteği gönderildi!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Gönderilen isteği listeye ekle
                    val updatedSentRequests = listAdapter.sentRequests.toMutableList()
                    updatedSentRequests.add(user.uid)
                    listAdapter.updateSentRequests(updatedSentRequests) // RecyclerView'i güncelle
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Arkadaşlık isteği gönderilirken bir hata oluştu!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        // Dialog oluştur ve göster
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("All Users")
            .setView(dialogView)
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        // Kullanıcı listesini gözlemlemeye gerek yok, çünkü `observeViewModel` bunu zaten yönetiyor
        checkUserList()
    }

    private fun observeFriendRequestStatus() {
        vm.friendRequestStatus.observe(viewLifecycleOwner) { statusMessage ->
            statusMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
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