package com.halil.chatapp.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.halil.chatapp.databinding.GetNotesListBinding
import com.halil.chatapp.ui.fragment.NotesListFragmentDirections

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

    inner class MyViewHolder(val binding: GetNotesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.notesImage.setOnClickListener {
                val note = notesList[adapterPosition]
                val action =
                    NotesListFragmentDirections.actionNotesListFragmentToWebViewFragment(note)
                it.findNavController().navigate(action)
            }
        }

        fun bind(note: String) {
            val uri = Uri.parse(note)
            binding.progressBar.visibility = View.VISIBLE
            Glide.with(binding.notesImage.context)
                .load(uri)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource != null) {
                            binding.notesImage.setImageDrawable(resource)
                        }
                        Glide.with(binding.notesImage.context).clear(binding.notesImage)
                        return true
                    }
                })
                .preload()
            binding.progressBar.visibility = View.GONE

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataChange(newDepartmentList: ArrayList<String>) {
        notesList.clear()
        notesList.addAll(newDepartmentList)
        notifyDataSetChanged()
    }
}