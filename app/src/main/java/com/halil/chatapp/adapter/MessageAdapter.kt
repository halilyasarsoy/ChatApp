package com.halil.chatapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.halil.chatapp.R
import com.halil.chatapp.data.Message
import com.halil.chatapp.databinding.ListItemBinding
import com.halil.chatapp.ui.fragment.ChatScreenFragment

class MessageAdapter(
    private val context: ChatScreenFragment,
    private var messageList: ArrayList<Message>
) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private lateinit var imageURI: Uri

    private val MESSAGGE_TYPE_LEFT = 0
    private val MESSAGGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null
    private var binding: ListItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.send_message, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.receive_message, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = messageList[position]
        holder.txtUserName.text = chat.message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (messageList[position].senderId == firebaseUser!!.uid) {
            return MESSAGGE_TYPE_RIGHT
        } else {
            return MESSAGGE_TYPE_LEFT
        }
    }
}