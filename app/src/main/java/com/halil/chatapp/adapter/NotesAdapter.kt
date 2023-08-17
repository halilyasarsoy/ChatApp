package com.halil.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        init {
            binding.notesImage.setOnClickListener {
                val note = notesList[adapterPosition]
                openUrlInBrowser(binding.root.context, note)
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
                        return false
                    }

                })
                .preload()
            binding.progressBar.visibility = View.GONE

        }
    }

    fun setDataChange(newDepartmentList: ArrayList<String>) {
        notesList.clear()
        notesList.addAll(newDepartmentList)
        notifyDataSetChanged()
    }

    private fun openUrlInBrowser(context: Context, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }
}