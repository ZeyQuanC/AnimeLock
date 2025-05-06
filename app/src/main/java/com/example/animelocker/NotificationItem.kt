package com.example.animelocker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationItem(
    val title: String,
    val message: String
) : Parcelable

