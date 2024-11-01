package com.example.animelocker


data class FeaturedAnimeResponse(
    val data: List<AnimeResponse>
)

data class AnimeResponse(
    val id: Int,
    val title: String?,
    val main_picture: MainPicture?,
    val synopsis: String?
)

data class MainPicture(
    val medium: String?
)
