package com.example.animelocker

// AnimeAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AnimeAdapter(
    private var animeList: List<Anime>,
    private val onAnimeClick: (Anime) -> Unit,
    private val onAnimeLongClick: (Anime) -> Unit // Add this parameter
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.bind(anime)
        holder.itemView.setOnClickListener { onAnimeClick(anime) }
        holder.itemView.setOnLongClickListener {
            onAnimeLongClick(anime)
            true
        }
    }

    override fun getItemCount(): Int = animeList.size

    fun updateData(newList: List<Anime>) {
        animeList = newList
        notifyDataSetChanged()
    }

    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.anime_title)
        private val image: ImageView = itemView.findViewById(R.id.anime_image)
        private val status: TextView = itemView.findViewById(R.id.anime_status)

        fun bind(anime: Anime) {
            // Bind your Anime data to the views here
            title.text = anime.title
            status.text = anime.status


            // Load image with Glide
            Glide.with(itemView.context)
                .load(anime.imageUrl)  // Assuming anime.imageUrl holds the correct image URL
                .placeholder(R.drawable.placeholder_image)  // Placeholder image while loading
                .into(image)
        }
    }
}
