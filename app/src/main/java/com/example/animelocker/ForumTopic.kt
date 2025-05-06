package com.example.animelocker

import com.google.gson.annotations.SerializedName


data class ForumTopic(
    val id: Int,
    val title: String,
    val created_at: String,
    val num_posts: Int,
    val created_by: Author? = null,  // Modify this line to include created_by
    val url: String?,
    val content: String?,
    var isExpanded: Boolean = false,
    val body: String? = null  // Add this to hold the content of the forum topic

)

data class Author(
    val id: Int,
    @SerializedName("name")
    val name: String?
)

data class ForumResponse(
    val data: List<ForumTopic>
)

