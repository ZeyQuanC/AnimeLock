package com.example.animelocker

// MyAnimeListApi.kt

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyAnimeListApi {

    @GET("anime/ranking")
    fun getAnimeRanking(
        @Query("rankingType") rankingType: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ): Call<List<AnimeResponse>>
}
