package com.example.animelocker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryFragment : Fragment() {

    private lateinit var recyclerViewFeatured: RecyclerView
    private lateinit var recyclerViewGenres: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView
    private lateinit var recyclerViewNewReleases: RecyclerView

    private var featuredAnimeList = mutableListOf<Anime>()
    private var genreList = mutableListOf<Genre>()
    private var trendingAnimeList = mutableListOf<Anime>()
    private var newReleasesList = mutableListOf<Anime>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views by ID
        recyclerViewFeatured = view.findViewById(R.id.recyclerViewFeatured)
        recyclerViewGenres = view.findViewById(R.id.recyclerViewGenres)
        recyclerViewTrending = view.findViewById(R.id.recyclerViewTrending)
        recyclerViewNewReleases = view.findViewById(R.id.recyclerViewNewReleases)

        // Initialize your RecyclerViews with adapters
        recyclerViewFeatured.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewGenres.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrending.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNewReleases.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Load anime data from the API
        fetchFeaturedAnime()
        fetchGenres(30230)
        fetchTrendingAnime()
        fetchNewReleases()

        // Set up search bar listener
        val searchBar = view.findViewById<EditText>(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchFeaturedAnime() {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getFeaturedAnime()

        Log.d("API Call", "Fetching featured anime from: $call")

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                Log.d("API Response", "Response code: ${response.code()}, Response body: ${response.body()}")

                if (response.isSuccessful) {
                    val featuredAnimeResponse = response.body()
                    if (featuredAnimeResponse != null) {
                        Log.d("API Response", "Featured Anime: $featuredAnimeResponse")

                        featuredAnimeList = featuredAnimeResponse.data.map { animeResponse ->
                            Anime(
                                id = animeResponse.node.id,
                                title = animeResponse.node.title ?: "Untitled",
                                imageUrl = animeResponse.node.main_picture.medium ?: "",
                                description = animeResponse.node.synopsis ?: "No description available"
                            )
                        }.toMutableList()

                        recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList) { anime ->
                            // Handle click event for featured anime
                        }
                    } else {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
                Log.e("API Failure", "Failed to fetch featured anime: ${t.message}")
            }
        })
    }



    private fun fetchGenres(animeId: Int) {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getAnimeGenres(animeId) // Call the method with the anime ID

        call.enqueue(object : Callback<GenresResponse> {
            override fun onResponse(call: Call<GenresResponse>, response: Response<GenresResponse>) {
                if (response.isSuccessful) {
                    genreList = response.body()?.data?.map { genreResponse ->
                        Genre(name = genreResponse.name)
                    }?.toMutableList() ?: mutableListOf()

                    recyclerViewGenres.adapter = GenreAdapter(genreList) { genre ->
                        // Handle click event for genre
                    }
                } else {
                    // Handle non-successful response
                    Log.e("API Error", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GenresResponse>, t: Throwable) {
                // Handle error
                Log.e("API Failure", "Failed to fetch genres: ${t.message}")
            }
        })
    }



    private fun fetchTrendingAnime() {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getAnimeRanking("airing", 50, "title,main_picture,synopsis") // Adjusted to use the correct parameters

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                if (response.isSuccessful) {
                    val trendingAnimeResponse = response.body()
                    trendingAnimeList = trendingAnimeResponse?.data?.map { animeRank ->  // Use animeRank here
                        val animeNode = animeRank.node  // Access the node property
                        Anime(
                            id = animeNode.id,  // Get id from animeNode
                            title = animeNode.title ?: "Untitled",  // Get title from animeNode
                            imageUrl = animeNode.main_picture.medium ?: "",  // Get medium image URL from main_picture
                            description = animeNode.synopsis ?: "No description available"  // Get synopsis from animeNode
                        )
                    }?.toMutableList() ?: mutableListOf()

                    recyclerViewTrending.adapter = AnimeAdapter(trendingAnimeList) { anime ->
                        // Handle click event for trending anime
                    }
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
                // Handle error
            }
        })
    }



    private fun fetchNewReleases() {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getAnimeRanking("upcoming", 50, "title,main_picture,synopsis") // Adjusted to use the upcoming ranking type

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                if (response.isSuccessful) {
                    val newReleasesResponse = response.body()
                    newReleasesList = newReleasesResponse?.data?.map { animeRank ->
                        // Navigate through the AnimeRank to get to the AnimeNode
                        val animeNode = animeRank.node
                        Anime(
                            id = animeNode.id,
                            title = animeNode.title ?: "Untitled",
                            imageUrl = animeNode.main_picture?.medium ?: "",
                            description = animeNode.synopsis ?: "No description available"
                        )
                    }?.toMutableList() ?: mutableListOf()

                    recyclerViewNewReleases.adapter = AnimeAdapter(newReleasesList) { anime ->
                        // Handle click event for new releases
                    }
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
                // Handle error
            }
        })
    }



    private fun performSearch(query: String) {
        // Implement search logic here
        if (query.isNotEmpty()) {
            val filteredList = featuredAnimeList.filter { it.title.contains(query, ignoreCase = true) }
            recyclerViewFeatured.adapter = AnimeAdapter(filteredList) { anime ->
                // Handle click event for filtered results
            }
        } else {
            recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList) { anime ->
                // Handle click event for featured anime
            }
        }
    }
}

