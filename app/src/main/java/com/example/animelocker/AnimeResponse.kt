package com.example.animelocker


data class featuredAnimeResponse(
    val data: List<AnimeResponse>
)

data class AnimeResponse(
    val id: Int,
    val title: String?,
    val main_picture: MainPicture?,
    val synopsis: String?,
    val start_date: String?,
    val end_date: String?,
    val media_type: String?,
    val rank: Int?,
    val status: String?,
    val genres: List<Genre>?,
    val num_episodes: Int?
)


data class MainPicture(
    val medium: String?
)

data class Genre(
    val id: Int,
    val name: String
)