package com.halil.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.databinding.GetDepartmentNameBinding

class DepartmentAdapter(private var departmentList: ArrayList<GetListUniversityNotes>) :
    RecyclerView.Adapter<DepartmentAdapter.MyViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(listOf: GetListUniversityNotes) {
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: GetDepartmentNameBinding =
            GetDepartmentNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val department = departmentList[position]
        holder.bind(department)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(department)
        }
    }

    override fun getItemCount(): Int {
        return departmentList.size
    }

    inner class MyViewHolder(private val binding: GetDepartmentNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(department: GetListUniversityNotes) {
            binding.departmentName.text = department.department
        }
    }

    fun setDataChange(newDepartmentList: ArrayList<GetListUniversityNotes>) {
        departmentList.clear()
        departmentList.addAll(newDepartmentList)
        notifyDataSetChanged()
    }
}