package com.example.animelocker

data class Discussion(
    val id: String = "",
    val username: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
