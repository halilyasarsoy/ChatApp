//package com.halil.chatapp.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.halil.chatapp.R
//
//class MesaggesAdapter(private val username: String, private var chats: List<Messages>) :
//    RecyclerView.Adapter<MesaggesAdapter.MessageViewHolder>() {
//
//    private val SENT = 0
//    private val RECEIVED = 1
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        val view = when (viewType) {
//            SENT -> {
//                LayoutInflater.from(parent.context).inflate(R.layout.send_message, parent, false)
//            }
//            else -> {
//                LayoutInflater.from(parent.context).inflate(R.layout.receive_message, parent, false)
//            }
//        }
//
//        return MessageViewHolder(view)
//    }
//
//    override fun getItemCount() = chats.size
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        holder.bind(chats[position])
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val firebaseUser= FirebaseAuth.getInstance().currentUser
//        return if (chats[position].sender==firebaseUser?.uid){
//            SENT
//        }else{
//            RECEIVED
//        }
//    }
//
//    fun updateData(chats: List<Messages>) {
//        this.chats = chats
//        notifyDataSetChanged()
//    }
//
//    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        private val chatMessage: TextView = itemView.findViewById(R.id.messageChats)
//        private val chatSender: TextView = itemView.findViewById(R.id.chat_sender)
//
//        fun bind(chat: Messages) {
//            chatMessage.text = chat.message
//
//            if (!username.contentEquals(chat.sender)) {
//                chatSender.text = chat.sender
//            }
//        }
//    }
//
//
//
//}