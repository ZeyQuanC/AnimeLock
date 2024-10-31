package com.example.animelocker

// MyAnimeListApi.kt

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyAnimeListApi {

    @GET("anime/ranking")
    fun getAnimeRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ): Call<AnimeRankingResponse> // Expecting AnimeRankingResponse here, not List<AnimeResponse>
}