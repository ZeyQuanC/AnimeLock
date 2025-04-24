package com.example.animelocker

data class AnimeRankingResponse(
    val data: List<AnimeRank>
) {
    data class AnimeRank(
        val node: AnimeNode,
        val ranking: Ranking
    )

    data class AnimeNode(
        val id: Int,
        val title: String,
        val main_picture: MainPicture,
        val synopsis: String?,
        val start_date: String?,
        val end_date: String?,
        val media_type: String?,
        val rank: Int?,
        val status: String?,
        val genres: List<Genre>?,
        val num_episodes: Int?
    )

    data class MainPicture(
        val medium: String,
        val large: String
    )

    data class Ranking(
        val rank: Int
    )

    data class Genre(
        val id: Int,
        val name: String
    )

}
