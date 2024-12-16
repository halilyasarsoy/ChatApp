package com.halil.chatapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

class ApprovedUsersAdapter(private var approvedUsers: ArrayList<Users>) :
    RecyclerView.Adapter<ApprovedUsersAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: ListAdapter.OnItemClickListener
    // Tıklama olayını dinlemek için arayüz
    interface OnItemClickListener : ListAdapter.OnItemClickListener {
        fun onItemClick(user: User)
        override fun onItemClick(user: Users) {
            TODO("Not yet implemented")
        }

        override fun onFriendRequestClick(user: Users) {
            TODO("Not yet implemented")
        }
    }


    // Listener'ı ayarlamak için fonksiyon
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            binding.tvFirstName.text = user.name
            binding.tvLastName.text = user.lastname
            binding.imgUser.load(user.imgUrl)
            binding.tvLatestMessage.text = user.profession

            itemView.setOnClickListener {
                if (::onItemClickListener.isInitialized) {
                    onItemClickListener.onItemClick(user)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(approvedUsers[position])
    }

    override fun getItemCount(): Int = approvedUsers.size

    fun updateApprovedUsers(newUsers: List<Users>) {
        Log.d("ApprovedUsersAdapter", "New approved users: ${newUsers.size}")

        approvedUsers.clear()
        approvedUsers.addAll(newUsers)
        notifyDataSetChanged()
    }
}
