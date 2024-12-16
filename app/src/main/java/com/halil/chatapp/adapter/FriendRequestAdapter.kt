package com.halil.chatapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.FriendRequestListItemBinding

class FriendRequestAdapter(
    private var requests: ArrayList<Users>,
    private val onApprove: (Users) -> Unit,
    private val onReject: (String) -> Unit // User yerine String kullanıyoruz
) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: FriendRequestListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            Log.d("FriendRequestAdapter", "Binding user: ${user.name} ${user.lastname}") // Kullanıcı bilgilerini logla

            binding.tvFirstName.text = user.name
            binding.tvLastName.text = user.lastname
            binding.imgUser.load(user.imgUrl)

            binding.btnApprove.setOnClickListener { onApprove(user) }
            binding.btnReject.setOnClickListener { onReject(user.uid) } // UID'yi gönderiyoruz
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FriendRequestListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    fun updateRequests(newRequests: List<Users>) {
        Log.d("FriendRequestAdapter", "New requests: ${newRequests.size}")

        requests.clear()
        requests.addAll(newRequests)
        notifyDataSetChanged()
    }
}
