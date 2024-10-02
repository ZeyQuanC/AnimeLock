package com.example.animelocker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var popularAdapter: AnimeAdapter
    private lateinit var recentActivityAdapter: AnimeAdapter
    private lateinit var recommendedAdapter: AnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        popularRecyclerView = view.findViewById(R.id.recycler_view_popular)
        recentActivityRecyclerView = view.findViewById(R.id.recycler_view_recent_activity)
        recommendedRecyclerView = view.findViewById(R.id.recycler_view_recommended)

        // Set layout managers for horizontal scrolling
        popularRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recentActivityRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Fetch anime data
        fetchPopularAnimeByPopularity()

        // Setup Bottom Navigation
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_watchlist -> {
                    findNavController().navigate(R.id.watchlistFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun fetchPopularAnimeByPopularity() {
        val apiKey = "ccf5d159aca09eaaed5c23f05fdd29f4" // Your API key
        val apiService = ApiClient.getClient(apiKey).create(MyAnimeListApi::class.java)
        val call = apiService.getAnimeRanking(
            rankingType = "bypopularity",
            limit = 50,
            fields = "title,main_picture,synopsis"
        )

        call.enqueue(object : Callback<List<AnimeResponse>> {
            override fun onResponse(call: Call<List<AnimeResponse>>, response: Response<List<AnimeResponse>>) {
                if (response.isSuccessful) {
                    val animeList = response.body()?.map {
                        Anime(
                            it.id,
                            it.title,
                            it.main_picture.medium, // Extract the medium image URL here
                            it.synopsis,

                        )
                    } ?: emptyList()

                    // Update the popular adapter
                    popularAdapter = AnimeAdapter(animeList) { anime -> onAnimeClicked(anime) }
                    popularRecyclerView.adapter = popularAdapter
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<AnimeResponse>>, t: Throwable) {
                Log.e("API Failure", t.message.toString())
            }
        })
    }



    // Remove or replace fetchRecentActivityAnime and fetchRecommendedAnime if not used yet
    // If you have API endpoints for these, implement them similar to fetchPopularAnime

    private fun onAnimeClicked(anime: Anime) {
        Log.d("HomeFragment", "Anime clicked: ${anime.title}")
        // Navigate to the detailed view for the selected anime
        // findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
    }
}


