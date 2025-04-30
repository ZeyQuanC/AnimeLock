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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        recentActivityRecyclerView = view.findViewById(R.id.recyclerViewRecentActivity)
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
            // Navigate to the user profile/settings screen
            findNavController().navigate(R.id.action_homeFragment_to_ProfileFragment)
        }

        val notificationButton = view.findViewById<ImageButton>(R.id.notification_button)
        notificationButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
        }

        loadRecentActivity()


    }


    private fun fetchPopularAnimeByPopularity() {
        val apiService = MyAnimeListClient.getApiService()

        val call = apiService.getAnimeRanking(
            rankingType = "bypopularity",
            limit = 50,
            fields = "id,title,synopsis,main_picture,start_date,end_date,media_type,rank,status,num_episodes" // Make sure to include synopsis if you use it
        )

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                if (response.isSuccessful) {

                    val animeRankingResponse = response.body()

                    val animeResponses = animeRankingResponse?.data ?: emptyList()

                    // Update parse function to take List<AnimeRankingResponse.AnimeRank>
                    val animeList = parseAnimeResponse(animeResponses)

                    popularAdapter = AnimeAdapter(animeList, { anime -> onAnimeClicked(anime) }, { anime -> onAnimeLongClicked(anime) })
                    popularRecyclerView.adapter = popularAdapter
                } else {
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
            }
        })
    }




    // Assuming you have a Firestore collection for user watchlists and each document has fields like 'status', 'animeId', 'title', etc.
    private fun loadRecentActivity() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.w("RecentActivity", "User is not logged in or UID is null")
            return
        }

        val userWatchlistRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("watchlist")

        val query = userWatchlistRef.whereEqualTo("status", "Watching")

        query.get()
            .addOnSuccessListener { documents ->
                Log.d("RecentActivity", "Firestore query succeeded, ${documents.size()} documents found")

                for (doc in documents) {
                    Log.d("RecentActivity", "Document data: ${doc.data}")
                }

                val recentActivityList = mutableListOf<Anime>()
                for (document in documents) {
                    val anime = document.toObject(Anime::class.java)
                    recentActivityList.add(anime)
                }

                updateRecentActivityRecyclerView(recentActivityList)
            }

            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }
    }




    // This function is used to update the RecyclerView with the recent activity data
    private fun updateRecentActivityRecyclerView(recentActivityList: List<Anime>) {
        Log.d("RecentActivity", "Attempting to update recent activity RecyclerView")

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewRecentActivity)

        if (recyclerView == null) {
            Log.w("RecentActivity", "RecyclerView not found! Make sure the ID is correct and the view is inflated.")
            return
        }

        Log.d("RecentActivity", "RecyclerView found, setting up adapter")

        val adapter = AnimeAdapter(
            recentActivityList,
            onAnimeClick = { anime ->
                Log.d("AnimeClick", "Clicked on ${anime.title}")
            },
            onAnimeLongClick = { anime ->
                Log.d("AnimeLongClick", "Long-clicked on ${anime.title}")
            }
        )

        // Set up the RecyclerView with a horizontal LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        Log.d("RecentActivity", "RecyclerView successfully updated with ${recentActivityList.size} items")
    }





    private fun fetchRecommendedAnime() {
        val apiService = MyAnimeListClient.getApiService()

        Log.d("API Request", "Fetching anime recommendations with limit 10")

        // Create the Authorization header with Bearer token
        val authHeader = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjkwMTZjZWRlMDNmYjk0YTdiMWRiMDVlNWZmNDkzOWU3YzM5ZjQ1ZTgxMzliYTczNWE3MjU4YWRiYmU0MjE2MGQ3MGQ3Njc2ZDAxYjIzMmYzIn0.eyJhdWQiOiJjY2Y1ZDE1OWFjYTA5ZWFhZWQ1YzIzZjA1ZmRkMjlmNCIsImp0aSI6IjkwMTZjZWRlMDNmYjk0YTdiMWRiMDVlNWZmNDkzOWU3YzM5ZjQ1ZTgxMzliYTczNWE3MjU4YWRiYmU0MjE2MGQ3MGQ3Njc2ZDAxYjIzMmYzIiwiaWF0IjoxNzQ1NDYxMTkyLCJuYmYiOjE3NDU0NjExOTIsImV4cCI6MTc0ODA1MzE5Miwic3ViIjoiMTc0NjA5NzAiLCJzY29wZXMiOltdfQ.NyJbWGooccf5lr1iFrkNyF5W8tCThafC-h0bJ62_uEdj8irbn3pErnE33DwPyL-uOy7d17TQ8ynH8OrrNwwJb9pfZkAjmhJr6wlpsb-ZZPHPihb7bTaMX35gra_x2bXh4ZJxyNTEuxGbX-h8-KpsOpQJ38AO1_0xnZBC_yNXytGTfKdlGylP545HWOEZqtSKO0cti5Ln6nx52NIx5mVWzE2hjMgfS-LNOOxXitNsASPWCS-Qgy4_vLOnvL7FEzufs7NOg9uZZtLAeQekB6lM8DtvW9r4jzTuQ9QvKXv-5OIuE2IfQeGpjBuIDXk7UADXyVXiXSxzrc5kLzdRdps-CA\n".trim()
        Log.d("Authorization Token2", authHeader)

        // Call the API endpoint for recommendations
        val call = apiService.getAnimeSuggestions(authHeader)

        call.enqueue(object : Callback<AnimeSuggestionsResponse> {
            override fun onResponse(call: Call<AnimeSuggestionsResponse>, response: Response<AnimeSuggestionsResponse>) {
                Log.d("API Response Code2", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("API Raw Response2", response.raw().toString())

                    val suggestionsResponse = response.body()
                    Log.d("API Parsed Response2", suggestionsResponse.toString())

                    val animeSuggestions = suggestionsResponse?.data ?: emptyList()
                    Log.d("API Recommendations List2", "Parsed ${animeSuggestions.size} anime items")

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
                    Log.e("API Error2", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AnimeSuggestionsResponse>, t: Throwable) {
                Log.e("API Failure2", "Failed to fetch recommendations: ${t.message}")
            }
        })
    }




    private fun parseAnimeResponse(animeResponses: List<AnimeRankingResponse.AnimeRank>): List<Anime> {
        Log.d("Parse Anime Response", "Converting ${animeResponses.size} AnimeRank objects to Anime objects")
        return animeResponses.map { animeRank ->
            animeRank.node.main_picture.let { mainPicture ->
                Log.d("Anime Conversion", "Mapping AnimeRank to Anime: ID=${animeRank.node.id}, Title=${animeRank.node.title}")
                Anime(
                    id = animeRank.node.id,
                    title = animeRank.node.title, // Default value for title
                    imageUrl = mainPicture.medium,
                    description = animeRank.node.synopsis, // Assuming synopsis is available in AnimeNode
                    num_episodes = animeRank.node.num_episodes ?: 0,
                    start_date = animeRank.node.start_date ?: "Unknown start date", // Default start date if null
                    end_date = animeRank.node.end_date ?: "Unknown end date", // Default end date if null
                    media_type = animeRank.node.media_type ?: "Unknown media type",
                    status = animeRank.node.status ?: "Unknown"

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

    // In your onAnimeClicked function
    fun onAnimeClicked(anime: Anime) {
        Log.d("Navigation", "Sending anime: $anime")
        val bundle = Bundle().apply {
            putParcelable("anime", anime) // Make sure you're passing the Anime object correctly
        }
        findNavController().navigate(R.id.action_homeFragment_to_animeDetailFragment, bundle)
    }



    // Handle long-click event
    fun onAnimeLongClicked(anime: Anime) {
        // Your long-click event handling logic
        Log.d("Long-click", "Long-clicked on: ${anime.title}")
    }




}
