package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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