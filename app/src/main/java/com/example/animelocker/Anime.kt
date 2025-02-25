package com.example.animelocker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    var id: Int = 0,                 // Default value required
    var title: String = "",
    var description: String = "",
    var imageUrl: String? = null,
    var status: String = "Unknown"
) : Parcelable {
    // Firestore requires an empty constructor
    constructor() : this(0, "", "", null, "Unknown")
}

