package com.example.animelocker

// ApiClient.kt

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.myanimelist.net/v2/" // Replace with actual base URL

    private var retrofit: Retrofit? = null

    fun getClient(apiKey: String): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("X-MAL-CLIENT-ID", apiKey) // Include the API key in headers
                        .method(original.method(), original.body())
                        .build()
                    chain.proceed(request)
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }
}
