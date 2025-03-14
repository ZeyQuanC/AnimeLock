package com.example.animelocker

import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.POST


interface AniListApi {
    @POST("https://graphql.anilist.co/") // Use POST for GraphQL requests
    suspend fun getAnimeEvents(
        @Body query: Map<String, String> // Sending query as Map (key: "query", value: actual query string)
    ): Response<AniListResponse>
}





