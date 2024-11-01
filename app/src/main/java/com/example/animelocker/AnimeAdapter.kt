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
    private val onAnimeClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.anime_title)
     //   val description: TextView = itemView.findViewById(R.id.anime_description)
        val image: ImageView = itemView.findViewById(R.id.anime_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.title.text = anime.title
     //   holder.description.text = anime.description

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(anime.imageUrl)
            .placeholder(R.drawable.placeholder_image)  // Placeholder image
            .into(holder.image)

        holder.itemView.setOnClickListener {
            it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            onAnimeClick(anime)
        }
    }

    override fun getItemCount() = animeList.size

    fun updateData(newList: List<Anime>) {
        animeList = newList
        notifyDataSetChanged()
    }
}
