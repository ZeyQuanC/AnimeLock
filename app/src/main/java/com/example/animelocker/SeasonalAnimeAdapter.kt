package com.example.animelocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SeasonalAnimeAdapter(private var animeList: MutableList<AnimeNode>, private val onAnimeClick: (AnimeNode) -> Unit) :
    RecyclerView.Adapter<SeasonalAnimeAdapter.AnimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.animeTitle.text = anime.title

        Glide.with(holder.itemView.context)
            .load(anime.main_picture?.medium) // Load anime image
            .into(holder.animeImage)

        holder.itemView.setOnClickListener { onAnimeClick(anime) }
    }

    override fun getItemCount() = animeList.size

    fun updateData(newList: List<AnimeNode>) {
        animeList.clear()
        animeList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.findViewById(R.id.anime_title)
        val animeImage: ImageView = itemView.findViewById(R.id.anime_image)
    }
}
