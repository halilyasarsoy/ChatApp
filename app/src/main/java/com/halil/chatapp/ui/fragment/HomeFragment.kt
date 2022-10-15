package com.halil.chatapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.R
import com.halil.chatapp.adapter.ListAdapter
import com.halil.chatapp.data.User
import com.halil.chatapp.ui.viewmodel.AuthViewModel
import com.halil.chatapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val vm: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList : ArrayList<User>
    private lateinit var listAdapter : ListAdapter
    private lateinit var db : FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        listAdapter = ListAdapter(userArrayList)
        recyclerView.adapter = listAdapter
        db = FirebaseFirestore.getInstance()
        eventChangeList()
    }
    private fun eventChangeList(){
        db.collection("users").get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (data in it.documents){
                        val user : User? = data.toObject(User::class.java)
                        if (user != null){
                            userArrayList.add(user)
                        }
                    }
                }
                recyclerView.adapter = ListAdapter(userArrayList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Toast", Toast.LENGTH_LONG).show()
            }


    }
}