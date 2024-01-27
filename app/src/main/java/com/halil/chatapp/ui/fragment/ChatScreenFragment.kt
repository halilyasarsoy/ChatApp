package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.halil.chatapp.adapter.MessageAdapter
import com.halil.chatapp.data.Message
import com.halil.chatapp.data.NotificationData
import com.halil.chatapp.data.PushNotification
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FragmentChatScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private val binding get() = _binding!!
    var messageList = ArrayList<Message>()
    private lateinit var databasex: DatabaseReference
    var firebaseUser: FirebaseUser? = null
    var topic = "user"
    private val args: ChatScreenFragmentArgs by navArgs()
    private var isNavigating = false

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.hide()
        supportActionBar?.setShowHideAnimationEnabled(false)
        _binding = FragmentChatScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageList = ArrayList()
        navigateToUsers()
        chatInfoSetup()
        navigateToDetail()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.recyclerViewChats.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        databasex = FirebaseDatabase.getInstance().getReference("users")
            .child(firebaseUser?.uid.orEmpty())
        databasex.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                println("${user?.name}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        databasex = FirebaseDatabase.getInstance().getReference("users").child(args.userId)
        binding.sendMessage.setOnClickListener {
            val message: String = binding.messageBox.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(context, "empty", Toast.LENGTH_SHORT)
                    .show()
                binding.messageBox.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, args.userId, message)
                binding.messageBox.setText("")
                topic = "/topics/${args.userId}"
                Log.d(this.javaClass.simpleName, "onCreate: topic: $topic, userName: ${args.name}")
                PushNotification(
                    NotificationData(args.name, message),
                    topic
                ).also {
                    //sendNotification(it)
                }
            }
        }
        readMessage(firebaseUser!!.uid, args.userId)
    }

    private fun chatInfoSetup() {
        binding.name.text = args.name
        binding.lastName.text = args.lastname
        binding.profileImage.load(args.imgUrl)
    }

    private fun navigateToUsers() {
        val imageView = binding.imageViewBackButton
        imageView.setOnClickListener {
            if (!isNavigating) {
                isNavigating = true
                val actionToUser =
                    ChatScreenFragmentDirections.actionChatScreenFragmentToHomeFragment()
                findNavController().navigate(actionToUser)
            }
        }

    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        reference.child("Message").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Message")
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Message::class.java)
                    if (chat?.senderId.equals(senderId) && chat?.receiverId.equals(receiverId) ||
                        chat?.senderId.equals(receiverId) && chat?.receiverId.equals(senderId)
                    ) {
                        if (chat != null) {
                            messageList.add(chat)
                        }
                    }
                }
                val chatAdapter = MessageAdapter(this@ChatScreenFragment, messageList)
                binding.recyclerViewChats.adapter = chatAdapter
                binding.recyclerViewChats.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun navigateToDetail() {
        val imageView = binding.profileImage
        imageView.setOnClickListener {
            val actionToUser =
                ChatScreenFragmentDirections.actionChatScreenFragmentToDetailUsersFragment(
                    args.name,
                    args.lastname,
                    args.imgUrl,
                    args.university
                )
            findNavController().navigate(actionToUser)
        }
    }

    override fun onStop() {
        super.onStop()
        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.show()
    }
}