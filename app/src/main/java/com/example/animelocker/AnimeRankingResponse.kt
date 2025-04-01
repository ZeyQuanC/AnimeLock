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
        val synopsis: String,
        val status: String
    )

    data class MainPicture(
        val medium: String,
        val large: String
    )

    data class Ranking(
        val rank: Int
    )
}
