package com.halil.chatapp.adapter

//import com.halil.chatapp.data.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

class ListAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var sentRequests = listOf<String>() // Gönderilen arkadaşlık isteklerinin UID'lerini tutar
    private lateinit var onItemClickListener: OnItemClickListener
    var approvedUsers = listOf<User>() // Arkadaş listesi

    interface OnItemClickListener {
        fun onItemClick(user: Users)
        fun onFriendRequestClick(user: Users)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun updateSentRequests(sentList: List<String>) {
        sentRequests = sentList
        notifyDataSetChanged() // RecyclerView'i günceller
    }

    fun updateApprovedUsers(approvedList: List<User>) {
        approvedUsers = approvedList
        notifyDataSetChanged()
    }
    fun updateItem(userId: String) {
        val position = userList.indexOfFirst { it.uid == userId }
        if (position != -1) {
            notifyItemChanged(position) // Sadece ilgili öğeyi günceller
        }
    }

    class MyViewHolder(
        private val itemBinding: ListItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(
            user: Users,
            isRequestSent: Boolean,
            isFriend: Boolean, // Arkadaşlık durumu
            listener: OnItemClickListener
        ) {
            itemBinding.tvFirstName.text = user.name
            itemBinding.tvLastName.text = user.lastname
            itemBinding.imgUser.load(user.imgUrl)

            // Buton görünürlüğünü arkadaşlık ve istek durumlarına göre ayarla
            if (isRequestSent || isFriend) {
                itemBinding.btnSendFriendRequest.visibility = View.GONE
            } else {
                itemBinding.btnSendFriendRequest.visibility = View.VISIBLE
                itemBinding.btnSendFriendRequest.setOnClickListener {
                    listener.onFriendRequestClick(user)
                }
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

        // Gönderilmiş isteği kontrol et
        val isRequestSent = sentRequests.contains(user.uid)

        // Arkadaşlık durumunu kontrol et
        val isFriend = approvedUsers.any { it.uid == user.uid }

        // Arkadaşlık veya gönderilmiş istek varsa butonu gizle
        holder.bindItem(user, isRequestSent, isFriend, onItemClickListener)
    }




    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserStatus(updatedUserList: ArrayList<Users>) {
        userList.clear()
        userList.addAll(updatedUserList)
        notifyDataSetChanged()
    }
}
