//package com.halil.chatapp.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.halil.chatapp.R
//import com.halil.chatapp.data.Message
//import com.halil.chatapp.databinding.ListItemBinding
//
//class MessageAdapter(
//    var context: Context,
//    messages: ArrayList<Message>?,
//    senderRoom: String,
//    receiverRoom: String
//) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
//    lateinit var messages: ArrayList<Message>
//    val ITEM_SENT = 1
//    val ITEM_REVEIVE = 2
//    var senderRoom: String
//    val receiverRoom: String
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//
//        return if (viewType == ITEM_SENT) {
//            val view : View = LayoutInflater.from(context).inflater(R.layout.)
//        } else {
//
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//    }
//
//    override fun getItemCount(): Int = messages.size
//
//    inner class SentMsg(private val itemBinding: ListItemBinding) :
//        RecyclerView.ViewHolder(itemBinding.root)
//
//    inner class ReceiveMsg(private val itemBinding: ListItemBinding) :
//        RecyclerView.ViewHolder(itemBinding.root)
//
//    init {
//        if (messages != null) {
//            this.messages = messages
//        }
//        this.senderRoom = senderRoom
//        this.receiverRoom = receiverRoom
//    }
//
//
//}