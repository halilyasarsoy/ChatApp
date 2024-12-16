package com.halil.chatapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ApprovedUserListBinding

class ApprovedUsersAdapter(
    private var approvedUsers: ArrayList<Users>,
    private var userStatuses: Map<String, String> = emptyMap() // Kullanıcı durumları
) :
    RecyclerView.Adapter<ApprovedUsersAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: ListAdapter.OnItemClickListener

    interface OnItemClickListener : ListAdapter.OnItemClickListener {

        override fun onItemClick(user: Users) {
            TODO("Not yet implemented")
        }

        override fun onFriendRequestClick(user: Users) {
            TODO("Not yet implemented")
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ApprovedUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            binding.tvFirstName.text = user.name
            binding.tvLastName.text = user.lastname
            binding.imgUser.load(user.imgUrl)
            binding.tvLatestMessage.text = user.profession

            val userStatus = userStatuses[user.uid] ?: "offline"
            if (userStatus == "online") {
                binding.statusOnlineIcon.visibility = View.VISIBLE
                binding.statusOfflineIcon.visibility = View.GONE
            } else {
                binding.statusOnlineIcon.visibility = View.GONE
                binding.statusOfflineIcon.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                if (::onItemClickListener.isInitialized) {
                    onItemClickListener.onItemClick(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ApprovedUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(approvedUsers[position])
    }

    override fun getItemCount(): Int = approvedUsers.size

    fun updateApprovedUsers(newUsers: List<Users>) {

        approvedUsers.clear()
        approvedUsers.addAll(newUsers)
        notifyDataSetChanged()
    }
    fun updateUserStatuses(newStatuses: Map<String, String>) {
        userStatuses = newStatuses
        notifyDataSetChanged()
    }
}
