package com.example.animelocker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WatchlistFragment : Fragment() {

    private lateinit var adapter: AnimeAdapter
    private lateinit var watchlist: List<Anime> // Assuming Anime is the model class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize your watchlist (replace with your actual data loading logic)
        watchlist = loadWatchlistData() // This should return a list of Anime objects

        // Set up the RecyclerView and Adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_watchlist)
        adapter = AnimeAdapter(watchlist) { anime -> onAnimeClick(anime) } // Pass click listener
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupFilterButtons()  // Set up your filter buttons here
    }

    private fun onAnimeClick(anime: Anime) {
        // Handle the click event, for example, navigate to details screen or show a toast
        Toast.makeText(requireContext(), "Clicked: ${anime.title}", Toast.LENGTH_SHORT).show()
    }

    private fun loadWatchlistData(): List<Anime> {
        // Load or fetch your anime watchlist data here
        // Replace this with your actual data fetching logic
        return listOf(
            Anime(1,"Naruto", "A story about ninjas", "A story about ninjas","Watching"),
            Anime(2,"Attack on Titan", "Giant humanoid titans","Giant humanoid titans", "Completed"),
            Anime(3,"One Piece", "Pirate adventure","Pirate adventure", "Planned"),
            Anime(4,"Death Note", "Notebook of death", "Notebook of death","Dropped")
        )
    }

    private fun setupFilterButtons() {
        val buttonFilterAll = view?.findViewById<Button>(R.id.button_filter_all)
        val buttonFilterWatching = view?.findViewById<Button>(R.id.button_filter_watching)
        val buttonFilterCompleted = view?.findViewById<Button>(R.id.button_filter_completed)
        val buttonFilterPlanned = view?.findViewById<Button>(R.id.button_filter_planned)
        val buttonFilterDropped = view?.findViewById<Button>(R.id.button_filter_dropped)

        buttonFilterAll?.setOnClickListener {
            adapter.updateData(watchlist)
        }

        buttonFilterWatching?.setOnClickListener {
            val filteredList = watchlist.filter { anime -> anime.status == "Watching" }
            adapter.updateData(filteredList)
        }

        buttonFilterCompleted?.setOnClickListener {
            val filteredList = watchlist.filter { anime -> anime.status == "Completed" }
            adapter.updateData(filteredList)
        }

        buttonFilterPlanned?.setOnClickListener {
            val filteredList = watchlist.filter { anime -> anime.status == "Planned" }
            adapter.updateData(filteredList)
        }

        buttonFilterDropped?.setOnClickListener {
            val filteredList = watchlist.filter { anime -> anime.status == "Dropped" }
            adapter.updateData(filteredList)
        }
    }
}


