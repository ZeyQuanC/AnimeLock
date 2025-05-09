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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryFragment : Fragment() {

    private lateinit var recyclerViewFeatured: RecyclerView
    private lateinit var recyclerViewSeasonal: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView
    private lateinit var recyclerViewNewReleases: RecyclerView
    private var featuredAnimeList = mutableListOf<Anime>()
    private var trendingAnimeList = mutableListOf<Anime>()
    private var newReleasesList = mutableListOf<Anime>()
    private var seasonalAnimeList = mutableListOf<Anime>()

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
        recyclerViewSeasonal = view.findViewById(R.id.recyclerViewSeasonal)
        recyclerViewTrending = view.findViewById(R.id.recyclerViewTrending)
        recyclerViewNewReleases = view.findViewById(R.id.recyclerViewNewReleases)

        // Initialize your RecyclerViews with adapters
        recyclerViewFeatured.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSeasonal.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrending.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNewReleases.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Load anime data from the API
        fetchFeaturedAnime()
        fetchSeasonalAnime(2025, "spring")
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
        val call = apiService.getFeaturedAnime(50, "id,title,synopsis,main_picture,start_date,end_date,media_type,rank,status,num_episodes")

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
                                title = animeResponse.node.title,
                                imageUrl = animeResponse.node.main_picture.medium,
                                description = animeResponse.node.synopsis,
                                media_type = animeResponse.node.media_type ?: "Unknown media type",
                                status = animeResponse.node.status,  // Status
                                num_episodes = animeResponse.node.num_episodes ?: 0,
                                start_date = animeResponse.node.start_date ?: "Unknown start date", // Default start date if null
                                end_date = animeResponse.node.end_date ?: "Unknown end date",

                                )
                        }.toMutableList()

                        // Inside fetchFeaturedAnime or wherever you're initializing the RecyclerView
                        recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList,
                            { anime -> onAnimeClicked(anime) }, // Handle click event for featured anime
                            { anime -> onAnimeLongClicked(anime) } // Optionally, handle long-click event
                        )


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



    private fun fetchSeasonalAnime(year: Int, season: String) {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getSeasonalAnime(year, season)

        Log.d("API Call", "Fetching seasonal anime from: $call")

        call.enqueue(object : Callback<SeasonalAnimeResponse> {
            override fun onResponse(call: Call<SeasonalAnimeResponse>, response: Response<SeasonalAnimeResponse>) {
                Log.d("API Response", "Response code: ${response.code()}, Response body: ${response.body()}")

                if (response.isSuccessful) {
                    val seasonalAnimeResponse = response.body()
                    if (seasonalAnimeResponse != null) {
                        Log.d("API Response", "Seasonal Anime: $seasonalAnimeResponse")

                        seasonalAnimeList = seasonalAnimeResponse.data.map { seasonalAnimeResponse ->
                            Anime(
                                id = seasonalAnimeResponse.node.id,
                                title = seasonalAnimeResponse.node.title,
                                imageUrl = seasonalAnimeResponse.node.main_picture?.medium,
                                description = seasonalAnimeResponse.node.synopsis ?: "No description available", // Provide a default if null

                            )
                        }.toMutableList()

                        // Set up the adapter with the fetched data
                        recyclerViewSeasonal.adapter = AnimeAdapter(seasonalAnimeList,
                            { anime -> onAnimeClicked(anime) }, // Handle click event for seasonal anime
                            { anime -> onAnimeLongClicked(anime) } // Optionally, handle long-click event
                        )
                    } else {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<SeasonalAnimeResponse>, t: Throwable) {
                Log.e("API Failure", "Failed to fetch seasonal anime: ${t.message}")
            }
        })
    }







    private fun fetchTrendingAnime() {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getAnimeRanking("airing", 50, "id,title,synopsis,main_picture,start_date,end_date,media_type,rank,status,num_episodes") // Adjusted to use the correct parameters

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                if (response.isSuccessful) {
                    val trendingAnimeResponse = response.body()
                    trendingAnimeList = trendingAnimeResponse?.data?.map { animeRank ->  // Use animeRank here
                        val animeNode = animeRank.node  // Access the node property
                        Anime(
                            id = animeNode.id,  // Get id from animeNode
                            title = animeNode.title,  // Get title from animeNode
                            imageUrl = animeNode.main_picture.medium,  // Get medium image URL from main_picture
                            description = animeNode.synopsis,  // Get synopsis from animeNode
                            status = animeNode.status ?: "Unknown",
                            num_episodes = animeNode.num_episodes ?: 0,
                            start_date = animeNode.start_date ?: "Unknown start date", // Default start date if null
                            end_date = animeNode.end_date ?: "Unknown end date", // Default end date if null
                            media_type = animeNode.media_type ?: "Unknown media type"
                        )
                    }?.toMutableList() ?: mutableListOf()

                    recyclerViewTrending.adapter = AnimeAdapter(trendingAnimeList,
                        { anime -> onAnimeClicked(anime) }, // Handle click event for featured anime
                        { anime -> onAnimeLongClicked(anime) } // Optionally, handle long-click event
                    )
                }
            }

            override fun onFailure(call: Call<AnimeRankingResponse>, t: Throwable) {
                // Handle error
            }
        })
    }



    private fun fetchNewReleases() {
        val apiService = MyAnimeListClient.getApiService()
        val call = apiService.getAnimeRanking("upcoming", 50, "id,title,synopsis,main_picture,start_date,end_date,media_type,rank,status,num_episodes")

        call.enqueue(object : Callback<AnimeRankingResponse> {
            override fun onResponse(call: Call<AnimeRankingResponse>, response: Response<AnimeRankingResponse>) {
                if (response.isSuccessful) {
                    val newReleasesResponse = response.body()
                    newReleasesList = newReleasesResponse?.data?.map { animeRank ->
                        // Accessing the AnimeNode from AnimeRank
                        val animeNode = animeRank.node
                        Anime(
                            id = animeNode.id, // Directly access `id` from AnimeNode
                            title = animeNode.title ?: "Untitled", // Fallback if title is null
                            imageUrl = animeNode.main_picture.medium ?: "", // Default empty if image is missing
                            description = animeNode.synopsis ?: "No description available.", // Default if synopsis is missing
                            status = animeNode.status ?: "Unknown", // Default if status is missing
                            rank = animeNode.rank ?: 0, // Default to 0 if rank is missing (as an integer)
                            num_episodes = animeNode.num_episodes ?: 0,
                            start_date = animeNode.start_date ?: "Unknown start date", // Default start date if null
                            end_date = animeNode.end_date ?: "Unknown end date", // Default end date if null
                            media_type = animeNode.media_type ?: "Unknown media type",
                        )


                    }?.toMutableList() ?: mutableListOf()

                    recyclerViewNewReleases.adapter = AnimeAdapter(newReleasesList,
                        { anime -> onAnimeClicked(anime) }, // Handle click event for featured anime
                        { anime -> onAnimeLongClicked(anime) } // Optionally, handle long-click event
                    )
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
            recyclerViewNewReleases.adapter = AnimeAdapter(newReleasesList, { anime ->
                // Handle click event for new releases
                Log.d("Anime Clicked", "Clicked on: ${anime.title}")
            }, { anime ->
                // Handle long-click event for new releases
                Log.d("Anime Long Clicked", "Long-clicked on: ${anime.title}")
            })

        } else {
            recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList,
                { anime -> onAnimeClicked(anime) }, // Handle click event for featured anime
                { anime -> onAnimeLongClicked(anime) } // Optionally, handle long-click event
            )

        }
    }

    // Handle click event
    fun onAnimeClicked(anime: Anime) {
        val bundle = Bundle().apply {
            putParcelable("anime", anime)
        }

        findNavController().navigate(R.id.action_discoveryFragment_to_animeDetailFragment, bundle)

        Log.d("Click", "Clicked on: ${anime.title}")
    }


    // Handle long-click event
    fun onAnimeLongClicked(anime: Anime) {
        // Your long-click event handling logic
        Log.d("Long-click", "Long-clicked on: ${anime.title}")
    }

}

