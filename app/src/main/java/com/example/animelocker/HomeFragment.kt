package com.example.animelocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        // Initialize MyAnimeListClient with context
        MyAnimeListClient.initialize(requireContext())

        // Fetch anime data
        fetchPopularAnimeByPopularity()
        fetchRecommendedAnime()  // Fetch recommendations

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
        // Handle user button click
        val userButton = view.findViewById<ImageButton>(R.id.user_button)
        userButton.setOnClickListener {
            Log.d("HomeFragment", "User button clicked")
            // Navigate to the user profile/settings screen
            findNavController().navigate(R.id.action_homeFragment_to_ProfileFragment)
        }


    }


    private fun fetchPopularAnimeByPopularity() {
        val apiService = MyAnimeListClient.getApiService()
        Log.d("API Request", "Fetching anime ranking by popularity with limit 50 and fields title, main_picture, synopsis")

        val call = apiService.getAnimeRanking(
            rankingType = "bypopularity",
            limit = 50,
            fields = "title, main_picture, synopsis" // Make sure to include synopsis if you use it
        )

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                Log.d("API Response Code", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("API Raw Response", response.raw().toString())

                    val animeRankingResponse = response.body()
                    Log.d("API Parsed Response", animeRankingResponse.toString())

                    val animeResponses = animeRankingResponse?.data ?: emptyList()
                    Log.d("API Anime List", "Parsed ${animeResponses.size} anime items")

                    // Update parse function to take List<AnimeRankingResponse.AnimeRank>
                    val animeList = parseAnimeResponse(animeResponses)
                    Log.d("Parsed Anime List", animeList.joinToString { "ID: ${it.id}, Title: ${it.title}" })

                    popularAdapter = AnimeAdapter(animeList, { anime -> onAnimeClicked(anime) }, { anime -> onAnimeLongClicked(anime) })
                    popularRecyclerView.adapter = popularAdapter
                    Log.d("Adapter", "Adapter updated with ${animeList.size} items")
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
                Log.e("API Failure", "Failed to fetch data: ${t.message}")
            }
        })
    }

    private fun fetchRecommendedAnime() {
        val apiService = MyAnimeListClient.getApiService()

        Log.d("API Request", "Fetching anime recommendations with limit 10")

        // Create the Authorization header with Bearer token
        val authHeader = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImFmOTA4MjdlMjEzNDNlNGNmZjRjNzA2MzZlYzQ3YjdmOWFiMGE0Mjc5NzU3MzMwMDJiNDkwMzM4ZDI1M2UwMGE2Y2Q2MmY1ZWE5MzBjZTAxIn0.eyJhdWQiOiJjY2Y1ZDE1OWFjYTA5ZWFhZWQ1YzIzZjA1ZmRkMjlmNCIsImp0aSI6ImFmOTA4MjdlMjEzNDNlNGNmZjRjNzA2MzZlYzQ3YjdmOWFiMGE0Mjc5NzU3MzMwMDJiNDkwMzM4ZDI1M2UwMGE2Y2Q2MmY1ZWE5MzBjZTAxIiwiaWF0IjoxNzQyNzQzNDY0LCJuYmYiOjE3NDI3NDM0NjQsImV4cCI6MTc0NTQyMTg2NCwic3ViIjoiMTc0NjA5NzAiLCJzY29wZXMiOltdfQ.KezasgRvzWj-8Unbl08HUfmEOvvuvWn1WhEd1xuy-X-3vyDCsJh6MQv8XtiZJDAckTiblAEdpcjOXC9vnU7Yet_jBtIiQWWGX-US322uKokRaxpcEir2f8dKl3smPVUraBBhii2Co0b7HLsAIr5pIElIjRrKGq1X20h_Y32gb_mh6y6nCjblCYeUJUpYd4h9waIj6ipiWm4oC04P2JPaINN0cJzXA52NNHNzXxGL_ABUH2xAhN7l1wlKhfWDoD39Y5USLM-9bylajs_OK6QIqyLUQse0vCTn_EZnXMXEMSj6hmrudm6ZbThFCWNzsmOhm8j5VpF9UjTLP0cGiLB_Rg\n".trim()
        Log.d("Authorization Token", authHeader)

        // Call the API endpoint for recommendations
        val call = apiService.getAnimeSuggestions(authHeader)

        call.enqueue(object : Callback<AnimeSuggestionsResponse> {
            override fun onResponse(call: Call<AnimeSuggestionsResponse>, response: Response<AnimeSuggestionsResponse>) {
                Log.d("API Response Code", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("API Raw Response", response.raw().toString())

                    val suggestionsResponse = response.body()
                    Log.d("API Parsed Response", suggestionsResponse.toString())

                    val animeSuggestions = suggestionsResponse?.data ?: emptyList()
                    Log.d("API Recommendations List", "Parsed ${animeSuggestions.size} anime items")

                    // Update parse function to convert List<AnimeSuggestion> to List<Anime>
                    val recommendedList = parseSuggestionsResponse(animeSuggestions)
                    Log.d("Parsed Recommendations List", recommendedList.joinToString { "ID: ${it.id}, Title: ${it.title}" })

                    // Setup the adapter with both click and long-click handlers
                    recommendedAdapter = AnimeAdapter(recommendedList,
                        { anime -> onAnimeClicked(anime) },  // onAnimeClick handler
                        { anime -> onAnimeLongClicked(anime) } // onAnimeLongClick handler
                    )
                    recommendedRecyclerView.adapter = recommendedAdapter
                    Log.d("Adapter", "Recommended adapter updated with ${recommendedList.size} items")
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AnimeSuggestionsResponse>, t: Throwable) {
                Log.e("API Failure", "Failed to fetch recommendations: ${t.message}")
            }
        })
    }




    private fun parseAnimeResponse(animeResponses: List<AnimeRankingResponse.AnimeRank>): List<Anime> {
        Log.d("Parse Anime Response", "Converting ${animeResponses.size} AnimeRank objects to Anime objects")
        return animeResponses.mapNotNull { animeRank ->
            animeRank.node.main_picture?.let { mainPicture ->
                Log.d("Anime Conversion", "Mapping AnimeRank to Anime: ID=${animeRank.node.id}, Title=${animeRank.node.title}")
                Anime(
                    id = animeRank.node.id,
                    title = animeRank.node.title, // Default value for title
                    imageUrl = mainPicture.medium,
                    description = animeRank.node.synopsis // Assuming synopsis is available in AnimeNode
                )
            }
        }
    }

    private fun parseSuggestionsResponse(suggestions: List<AnimeSuggestionsResponse.AnimeSuggestion>): List<Anime> {
        Log.d("Parse Suggestions Response", "Converting ${suggestions.size} AnimeSuggestion objects to Anime objects")
        return suggestions.mapNotNull { suggestion ->
            suggestion.node.main_picture?.let { mainPicture ->
                Log.d("Suggestions Conversion", "Mapping AnimeSuggestion to Anime: ID=${suggestion.node.id}, Title=${suggestion.node.title ?: "Unknown"}")
                Anime(
                    id = suggestion.node.id,
                    title = suggestion.node.title ?: "Untitled", // Default value for title
                    imageUrl = mainPicture.medium,
                    description = "No description available" // Default description since this endpoint might not return one
                )
            }
        }
    }

    // Handle click event
    fun onAnimeClicked(anime: Anime) {
        val bundle = Bundle().apply {
            putParcelable("anime", anime)
        }

        findNavController().navigate(R.id.action_homeFragment_to_animeDetailFragment, bundle)

        Log.d("Click", "Clicked on: ${anime.title}")
    }


    // Handle long-click event
    fun onAnimeLongClicked(anime: Anime) {
        // Your long-click event handling logic
        Log.d("Long-click", "Long-clicked on: ${anime.title}")
    }

}
