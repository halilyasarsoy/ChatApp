package com.halil.chatapp.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.halil.chatapp.R

class ListAdapter(private val userList : ArrayList<com.halil.chatapp.data.User>) : RecyclerView.Adapter<ListAdapter.MyViewHolder> () {

    class MyViewHolder (itemView : View): RecyclerView.ViewHolder(itemView){

    val firstName : TextView = itemView.findViewById(R.id.tvFirstName)
    val lastName : TextView = itemView.findViewById(R.id.tvLastName)
    val email : TextView = itemView.findViewById(R.id.tvEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.firstName.text = userList[position].name
        holder.lastName.text = userList[position].lastname
        holder.email.text = userList[position].email
    }

    override fun getItemCount(): Int {
     return   userList.size
    }
}