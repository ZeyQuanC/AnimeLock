package com.example.animelocker

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyAnimeListClient {
    private lateinit var apiService: MyAnimeListApi

    fun initialize(context: Context) {
        val clientId = context.getString(R.string.mal_client_id)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("X-MAL-CLIENT-ID", clientId) // Add your client ID header
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.myanimelist.net/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Create the API service
        apiService = retrofit.create(MyAnimeListApi::class.java)
    }

    // Method to get the API service
    fun getApiService(): MyAnimeListApi {
        return apiService
    }
}

