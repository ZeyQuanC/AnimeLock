package com.example.animelocker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    var id: Int = 0,
    var title: String = "",
    var description: String? = null,          // Renamed to match the API response
    var imageUrl: String? = null,  // Holds the image URLs (medium, large)
    var start_date: String? = null,        // Start date
    var end_date: String? = null,          // End date
    var media_type: String? = null,        // Type of media (TV, Movie, etc.)
    var rank: Int? = null,                 // Rank (score)
    var status: String? = null,            // Status of airing (e.g., finished_airing)
    var num_episodes: Int? = null          // Number of episodes
) : Parcelable {
    constructor() : this(
        0, "", null, null, null, null, null, null, null, null
    )
}

