package com.halil.chatapp.ui.fragment

//import com.halil.chatapp.data.Message
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FragmentHomeBinding
import com.halil.chatapp.other.Resource
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val vm: MainViewModel by viewModels()
    private var userArrayList: ArrayList<Users>? = null
    private val listAdapter = ListAdapter(arrayListOf())
    private lateinit var db: FirebaseFirestore
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var headerView: View

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
        headerView = requireActivity().findViewById(R.id.navDrawView)

        change()
    }

    private fun checkUserApproved(user: Users, onResult: (Boolean) -> Unit) {
        if (user.approved) {
            onResult.invoke(true) // Kullanıcı onaylıysa, mesaj gönderilebilir
        } else {
            onResult.invoke(false) // Kullanıcı onaylı değilse, mesaj gönderilemez
        }
    }

    private fun adapterSetup() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)

            adapter = listAdapter
        }

        listAdapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onItemClick(user: Users) {
                checkUserApproved(user) { approved ->
                    if (approved) {
                        val direction = user.imgUrl?.let {
                            HomeFragmentDirections.actionHomeFragmentToChatScreenFragment(
                                user.name,
                                user.lastname,
                                it,
                                user.uid
                            )
                        }
                        if (direction != null) {
                            findNavController().navigate(direction)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Bu kullanıcı henüz onaylanmadı!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                    //progresbar
                }
                is Resource.Success -> {
                    val userList = it.data as? ArrayList<Users>
                    val sortedList = userList?.sortedByDescending { user -> user.status }
                    sortedList?.let { newList ->
                        listAdapter.updateUserStatus(ArrayList(newList))
                    }
                }
                else -> {}
            }
        }
    }

    private fun change() {
        vm.fetchNotesData(requireContext())
        vm.universityData.observe(viewLifecycleOwner) { universities ->
            val denemetext: TextView = headerView.findViewById(R.id.deneme)
            if (universities.size >= 2) {
                val university = universities[0]
                val department = universities[1]
                denemetext.text = "$university - $department"
            }
        }
    }
}


