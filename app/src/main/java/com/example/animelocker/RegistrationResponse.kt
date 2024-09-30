package com.example.animelocker

data class RegistrationResponse(
    val status: String,         // e.g., "success" or "error"
    val message: String? = null // Optional error message
)
