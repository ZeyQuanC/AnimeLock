package com.example.animelocker

// Anime.kt
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val description: String, // Make sure this is defined if you want to use it
)

