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


    @GET("anime/ranking?ranking_type=all&limit=50")
    // Adjust the endpoint as needed
    fun getFeaturedAnime(
        @Query("limit") limit: Int = 50,
        @Query("fields") fields: String = "title,main_picture,synopsis"
    ): Call<AnimeRankingResponse>

    // Method to fetch genres
    @GET("anime/genres") // Adjust the endpoint as needed
    fun getAnimeGenres(): Call<GenresResponse>



}
