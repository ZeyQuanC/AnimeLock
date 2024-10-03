package com.example.animelocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GenreAdapter(
    private var genreList: List<Genre>,  // Use var to allow updating the list
    private val onGenreClick: (Genre) -> Unit
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genreName: TextView = itemView.findViewById(R.id.genre_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.genreName.text = genre.name
        holder.itemView.setOnClickListener { onGenreClick(genre) }
    }

    override fun getItemCount() = genreList.size

    // Method to update the data list and notify the adapter
    fun updateData(newList: List<Genre>) {
        genreList = newList
        notifyDataSetChanged()  // Notify the adapter that the data set has changed
    }
}
