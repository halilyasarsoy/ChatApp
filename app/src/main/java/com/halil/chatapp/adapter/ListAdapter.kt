package com.halil.chatapp.adapter

import android.app.LauncherActivity.ListItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.halil.chatapp.R
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.ListItemBinding

//binding
//click
//??
class ListAdapter(var userList : ArrayList<Users>) : RecyclerView.Adapter<ListAdapter.MyViewHolder> () {

     class MyViewHolder (private val itemBinding : ListItemBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
         fun bindItem (users : Users){
             itemBinding.tvFirstName.text = users.name
             itemBinding.tvLastName.text = users.lastname
         }
//    val firstName : TextView = itemView.findViewById(R.id.tvFirstName)
//    val lastName : TextView = itemView.findViewById(R.id.tvLastName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
     return MyViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      val user = userList[position]
        holder.bindItem(user)
    }

    override fun getItemCount(): Int {
     return userList.size
    }

    fun setDataChange (newUserList : List<Users>){
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }
}