package com.example.animelocker

// User.kt

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String // You might not want to store this directly in the model
)
