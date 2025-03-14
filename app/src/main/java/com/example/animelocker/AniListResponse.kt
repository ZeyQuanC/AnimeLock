package com.example.animelocker

data class AniListResponse(
    val data: PageData
)

data class PageData(
    val Page: Page
)

data class Page(
    val media: List<Media>
)

data class Media(
    val title: Title,
    val startDate: AnimeStartDate?
)

data class Title(
    val romaji: String?
)

data class AnimeStartDate(
    val year: Int?,
    val month: Int?,
    val day: Int?
) {
    // Convert AnimeStartDate to a string
    fun toFormattedString(): String {
        return if (year != null && month != null && day != null) {
            "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        } else {
            "Unknown"
        }
    }
}
