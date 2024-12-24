package com.halil.chatapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

class ListAdapter(
    private var userList: ArrayList<Users>,
    private var userStatuses: Map<String, String> = emptyMap()
) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var sentRequests = listOf<String>()
    private lateinit var onItemClickListener: OnItemClickListener
    private var approvedUsers = listOf<Users>() // Onaylanmış arkadaşlar

    interface OnItemClickListener {
        fun onItemClick(user: Users)
        fun onFriendRequestClick(user: Users)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class MyViewHolder(private val itemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(
            user: Users,
            isRequestSent: Boolean,
            isFriend: Boolean,
            listener: OnItemClickListener
        ) {
            itemBinding.tvFirstName.text = user.name
            itemBinding.tvLastName.text = user.lastname
            itemBinding.tvLatestMessage.text = user.profession
            itemBinding.imgUser.load(user.imgUrl)

            val userStatus = userStatuses[user.uid] ?: "offline"
            itemBinding.statusOnlineIcon.visibility = if (userStatus == "online") View.VISIBLE else View.GONE
            itemBinding.statusOfflineIcon.visibility = if (userStatus != "online") View.VISIBLE else View.GONE

            // Arkadaşlık isteği butonunu yönet
            val isAlreadyFriend = approvedUsers.any { it.uid == user.uid }
            itemBinding.btnSendFriendRequest.visibility =
                if (isRequestSent || isFriend || isAlreadyFriend) View.GONE else View.VISIBLE

            itemBinding.btnSendFriendRequest.setOnClickListener {
                listener.onFriendRequestClick(user)
            }

            // Item tıklama
            itemView.setOnClickListener {
                listener.onItemClick(user)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSentRequests(sentList: List<String>) {
        sentRequests = sentList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateApprovedUsers(approvedList: List<Users>) {
        approvedUsers = approvedList
        notifyDataSetChanged()
    }

    fun updateItem(userId: String) {
        val position = userList.indexOfFirst { it.uid == userId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserStatus(newUserList: ArrayList<Users>) {
        userList.clear()
        userList.addAll(newUserList)
        sortUsersByStatus()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserStatuses(newStatuses: Map<String, String>) {
        userStatuses = newStatuses
        sortUsersByStatus()
        notifyDataSetChanged()
    }

    private fun sortUsersByStatus() {
        userList.sortWith { user1, user2 ->
            val status1 = userStatuses[user1.uid] ?: "offline"
            val status2 = userStatuses[user2.uid] ?: "offline"
            when {
                status1 == "online" && status2 != "online" -> -1
                status1 != "online" && status2 == "online" -> 1
                else -> 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]

        val isRequestSent = sentRequests.contains(user.uid)
        val isFriend = approvedUsers.any { it.uid == user.uid }

        holder.bindItem(user, isRequestSent, isFriend, onItemClickListener)
    }

    override fun getItemCount(): Int = userList.size
}
