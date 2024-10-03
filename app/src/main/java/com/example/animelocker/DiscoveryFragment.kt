package com.example.animelocker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DiscoveryFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    // Define lists to hold your anime data
    private var featuredAnimeList = mutableListOf<Anime>()
    private var genreList = mutableListOf<Genre>() // Update to hold Genre objects
    private var trendingAnimeList = mutableListOf<Anime>()
    private var newReleasesList = mutableListOf<Anime>()

    private lateinit var recyclerViewFeatured: RecyclerView
    private lateinit var recyclerViewGenres: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView
    private lateinit var recyclerViewNewReleases: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

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

        // Load anime data from Firestore
        loadAnimeData()

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

    private fun loadAnimeData() {
        // Load featured anime
        firestore.collection("featuredAnime").get()
            .addOnSuccessListener { result ->
                featuredAnimeList = result.toObjects(Anime::class.java).toMutableList()
                recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList) { anime ->
                    // Handle click event for featured anime
                }
            }

        // Load genres
        firestore.collection("genres").get()
            .addOnSuccessListener { result ->
                genreList = result.documents.map { document ->
                    Genre(
                        name = document.getString("name") ?: ""
                    )
                }.toMutableList() // Create Genre objects from Firestore data
                recyclerViewGenres.adapter = GenreAdapter(genreList) { genre ->
                    // Handle click event for genre
                }
            }

        // Load trending anime
        firestore.collection("trendingAnime").get()
            .addOnSuccessListener { result ->
                trendingAnimeList = result.toObjects(Anime::class.java).toMutableList()
                recyclerViewTrending.adapter = AnimeAdapter(trendingAnimeList) { anime ->
                    // Handle click event for trending anime
                }
            }

        // Load new releases
        firestore.collection("newReleases").get()
            .addOnSuccessListener { result ->
                newReleasesList = result.toObjects(Anime::class.java).toMutableList()
                recyclerViewNewReleases.adapter = AnimeAdapter(newReleasesList) { anime ->
                    // Handle click event for new releases
                }
            }
    }

    private fun performSearch(query: String) {
        // Implement search logic here
        if (query.isNotEmpty()) {
            // Filter your lists based on the search query
            val filteredList = featuredAnimeList.filter { it.title.contains(query, ignoreCase = true) }
            recyclerViewFeatured.adapter = AnimeAdapter(filteredList) { anime ->
                // Handle click event for filtered results
            }
        } else {
            // Reset the adapter when the search query is empty
            recyclerViewFeatured.adapter = AnimeAdapter(featuredAnimeList) { anime ->
                // Handle click event for featured anime
            }
        }
    }
}

