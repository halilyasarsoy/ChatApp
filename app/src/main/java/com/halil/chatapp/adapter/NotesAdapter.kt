package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halil.chatapp.databinding.GetNotesListBinding

class NotesAdapter(private var notesList: ArrayList<String>) :
    RecyclerView.Adapter<NotesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: GetNotesListBinding =
            GetNotesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notes = notesList[position]
        holder.bind(notes)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class MyViewHolder(private val binding: GetNotesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: String) {
            binding.notesList.text = note
        }
    }

    fun setDataChange(newDepartmentList: ArrayList<String>) {
        notesList.clear()
        notesList.addAll(newDepartmentList)
        notifyDataSetChanged()
    }
}