package com.example.animelocker

data class Event(
    val title: String,
    val startDate: AnimeStartDate, // Assuming startDate is a string in the format "YYYY-MM-DD"
    val description: String,
    var isExpanded: Boolean = false
)



