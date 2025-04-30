package com.example.animelocker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WatchlistFragment : Fragment() {

    private lateinit var adapter: AnimeAdapter
    private var watchlist: List<Anime> = emptyList() // List of Anime to display

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the watchlist from Firestore
        loadWatchlistData()

        // Set up RecyclerView and Adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_watchlist)
        adapter = AnimeAdapter(watchlist, { anime -> onAnimeClick(anime) }, { anime -> removeAnimeFromWatchlist(anime) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Set up filter buttons
        setupFilterButtons(view)
    }

    private fun onAnimeClick(anime: Anime) {
        // Add anime to the watchlist
        addAnimeToWatchlist(anime)
        // Handle the anime click event
        Toast.makeText(requireContext(), "Clicked: ${anime.title}", Toast.LENGTH_SHORT).show()
    }

    // Fetch the watchlist data from Firestore
    private fun loadWatchlistData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .collection("watchlist")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Failed to fetch watchlist", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val updatedWatchlist = snapshots.documents.map { it.toObject(Anime::class.java)!! }
                    watchlist = updatedWatchlist
                    adapter.updateData(watchlist)
                }
            }
    }

    // Add an anime to Firestore
    private fun addAnimeToWatchlist(anime: Anime) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .collection("watchlist").document(anime.id.toString())
            .set(anime)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${anime.title} added to watchlist", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add anime", Toast.LENGTH_SHORT).show()
            }
    }

    // Remove an anime from Firestore
    private fun removeAnimeFromWatchlist(anime: Anime) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .collection("watchlist").document(anime.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${anime.title} removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to remove anime", Toast.LENGTH_SHORT).show()
            }
    }

    // Set up filter buttons to filter the watchlist by status
    private fun setupFilterButtons(view: View) {
        val buttonFilterAll = view.findViewById<Button>(R.id.button_filter_all)
        val buttonFilterWatching = view.findViewById<Button>(R.id.button_filter_watching)
        val buttonFilterCompleted = view.findViewById<Button>(R.id.button_filter_completed)
        val buttonFilterPlanned = view.findViewById<Button>(R.id.button_filter_planned)
        val buttonFilterDropped = view.findViewById<Button>(R.id.button_filter_dropped)

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


