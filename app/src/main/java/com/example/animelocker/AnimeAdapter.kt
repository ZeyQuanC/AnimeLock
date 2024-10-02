package com.example.animelocker

// AnimeAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnimeAdapter(
    private val animeList: List<Anime>,
    private val onAnimeClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.anime_title)
        val description: TextView = itemView.findViewById(R.id.anime_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.title.text = anime.title
        holder.description.text = anime.description
        holder.itemView.setOnClickListener { onAnimeClick(anime) }
    }

    override fun getItemCount() = animeList.size
}
