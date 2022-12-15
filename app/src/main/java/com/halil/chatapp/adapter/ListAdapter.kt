package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
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

    class MyViewHolder(private val itemBinding: ListItemBinding, listener: OnItemClickListener) :
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
                LayoutInflater.from(parent.context), parent, false
            ), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(userList)
        val userList = userList[position]

        holder.itemView.apply {
            binding?.tvFirstName?.text = userList.name
            binding?.tvLastName?.text = userList.lastname
            binding?.imgUser?.load(userList.imgUrl)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(userList)
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