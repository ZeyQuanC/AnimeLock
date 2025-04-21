package com.example.animelocker

// MyAnimeListApi.kt

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
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

    @GET("anime/season/{year}/{season}")
    fun getSeasonalAnime(
        @Path("year") year: Int,
        @Path("season") season: String,
        @Query("fields") fields: String = "id,title,main_picture"
    ): Call<SeasonalAnimeResponse>


    @GET("anime/suggestions")
    fun getAnimeSuggestions(@Header("Authorization") authHeader: String): Call<AnimeSuggestionsResponse>

    @GET("forum/topics")
    suspend fun getForumTopics(
        @Query("q") query: String = "anime",
        @Query("limit") limit: Int = 30
    ): ForumResponse





}
