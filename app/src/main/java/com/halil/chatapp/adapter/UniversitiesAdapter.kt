package com.halil.chatapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halil.chatapp.data.NotesData
import com.halil.chatapp.data.Users
import com.halil.chatapp.databinding.GetNotesBinding

class UniversityAdapter(private var universitiesList: List<NotesData>) :
    RecyclerView.Adapter<UniversityAdapter.MyViewHolder>() {

    private lateinit var onItemClickListener: ListAdapter.OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(universities: NotesData)
    }

    fun setUniversities(universities: List<NotesData>) {
        universitiesList = universities
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ListAdapter.OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: GetNotesBinding = GetNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val university = universitiesList[position]
        holder.bind(university)
    }

    override fun getItemCount(): Int {
        return universitiesList.size
    }

    inner class MyViewHolder(private val binding: GetNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(university: NotesData) {
            binding.universityName.text = university.university
            binding.departmentName.text = university.department
        }
    }
}

