package com.example.animelocker

data class GenresResponse(
    val data: List<GenreResponse>
)

data class GenreResponse(
    val id: Int, // Include an ID if necessary
    val name: String
)
