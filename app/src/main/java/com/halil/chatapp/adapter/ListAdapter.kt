package com.halil.chatapp.adapter

//import com.halil.chatapp.data.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

class ListAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var binding: ListItemBinding? = null
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(user: Users) {
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class MyViewHolder(
        private val itemBinding: ListItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(user: Users) {
            itemBinding.tvFirstName.text = user.name
            itemBinding.tvLastName.text = user.lastname
            itemBinding.imgUser.load(user.imgUrl)
            itemBinding.tvLatestMessage.text = user.profession

            if (user.status) {
                itemBinding.statusOnlineIcon.visibility = View.VISIBLE
                itemBinding.statusOfflineIcon.visibility = View.GONE
            } else {
                itemBinding.statusOnlineIcon.visibility = View.GONE
                itemBinding.statusOfflineIcon.visibility = View.VISIBLE
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
        FirebaseDatabase.getInstance().getReference("User-Status").child(user.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userStatus = snapshot.child("status").getValue(String::class.java)
                    user.status = userStatus == "online"
                    userList.sortByDescending { it.status }
                    holder.bindItem(user)

                }

                override fun onCancelled(error: DatabaseError) {
                    // handle error
                }
            })
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserStatus(updatedUserList: ArrayList<Users>) {

        // Kullanıcı listesini güncelle
        userList.clear()
        userList.addAll(updatedUserList)
        // RecyclerView'e değişiklikleri bildir
        notifyDataSetChanged()
    }
}
