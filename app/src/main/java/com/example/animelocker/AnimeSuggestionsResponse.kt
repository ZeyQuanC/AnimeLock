package com.example.animelocker

data class AnimeSuggestionsResponse(
    val data: List<AnimeSuggestion>
) {

    data class AnimeSuggestion(
        val node: AnimeNode // Adjust according to your data structure
    )

    data class AnimeNode(
        val id: Int,
        val title: String?,
        val main_picture: MainPicture?
    )

    data class MainPicture(
        val medium: String

    )
}