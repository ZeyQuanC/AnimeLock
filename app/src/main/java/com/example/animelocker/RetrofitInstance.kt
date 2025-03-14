package com.example.animelocker

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Define the base URL for AniList API
    private const val BASE_URL = "https://graphql.anilist.co/"

    // Create the Retrofit instance using lazy initialization
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Set the base URL
        .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json") // Ensure Content-Type is set
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        ) // Optional: Add logging/interceptor if needed
        .build()

    // Create the API service instance using lazy initialization
    val api: AniListApi = retrofit.create(AniListApi::class.java)
}


