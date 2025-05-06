package com.example.animelocker

data class SeasonalAnimeResponse(
    val data: List<AnimeData>
)

data class AnimeData(
    val node: AnimeNode
)

data class AnimeNode(
    val id: Int,
    val title: String,
    val main_picture: AnimePicture?,
    val synopsis: String

    )

data class AnimePicture(
    val medium: String,
    val large: String
)
