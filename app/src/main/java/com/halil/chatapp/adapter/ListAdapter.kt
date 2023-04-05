package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.chatapp.data.User
//import com.halil.chatapp.data.Message
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

class ListAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var binding: ListItemBinding? = null
    private lateinit var onItemClickListener: OnItemClickListener
    private var searchText = ""

    interface OnItemClickListener {
        fun onItemClick(user: Users) {
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class MyViewHolder(private val itemBinding: ListItemBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(users: ArrayList<Users>) {
            val userX = users[adapterPosition]
            itemBinding.tvFirstName.text = userX.name
            itemBinding.tvLastName.text = userX.lastname
            itemBinding.imgUser.load(userX.imgUrl)
            itemBinding.tvLatestMessage.text= userX.profession
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        if (user.name.contains(searchText, true) || user.lastname.contains(searchText, true)) {
            holder.bindItem(userList)
            holder.itemView.apply {
                binding?.tvFirstName?.text = user.name
                binding?.tvLastName?.text = user.lastname
                binding?.imgUser?.load(user.imgUrl)
                binding?.tvLatestMessage?.text=user.profession
            }
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemClick(user)
            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    fun setSearchText(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    fun setDataChange(newUserList: ArrayList<Users>) {
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }
}