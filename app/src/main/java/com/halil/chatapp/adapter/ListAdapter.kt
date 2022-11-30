package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

//binding
//click
//??
class ListAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var onItemClick: ((Users) -> Unit)? = null

    class MyViewHolder(private val itemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(users: ArrayList<Users>) {
            val userX = users[adapterPosition]
            itemBinding.tvFirstName.text = userX.name
            itemBinding.tvLastName.text = userX.lastname
            itemBinding.imgUser.load(userX.imgUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.bindItem(userList)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setDataChange(newUserList: List<Users>) {
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }
}