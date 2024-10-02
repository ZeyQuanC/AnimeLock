package com.example.animelocker

data class AnimeResponse(
    val id: Int,
    val title: String,
    val main_picture: MainPicture,
    val synopsis: String
)

data class MainPicture(
    val medium: String,
    val large: String
)
