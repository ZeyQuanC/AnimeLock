package com.example.animelocker

data class Event(
    val eventName: String = "",
    val eventDate: String = "",  // In "dd/MM/yyyy" format
    val eventTime: String = ""   // Optional: You can add an event time if needed
)
