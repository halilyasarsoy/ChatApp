package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.databinding.GetNotesBinding

class UniversityAdapter(private var universityNameList: ArrayList<GetListUniversityNotes>) :
    RecyclerView.Adapter<UniversityAdapter.MyViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(university: GetListUniversityNotes) {
        }
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: GetNotesBinding =
            GetNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val university = universityNameList[position]
        holder.bind(university)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(university)
        }
    }

    override fun getItemCount(): Int {
        return universityNameList.size
    }

    inner class MyViewHolder(private val binding: GetNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(university: GetListUniversityNotes) {
            binding.universityName.text = university.university

        }
    }

    fun setDataChange(newUniversityList: ArrayList<GetListUniversityNotes>) {
        universityNameList.clear()
        universityNameList.addAll(newUniversityList)
        notifyDataSetChanged()
    }
}


