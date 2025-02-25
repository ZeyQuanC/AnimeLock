package com.example.animelocker

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnimeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anime_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val anime: Anime? = arguments?.getParcelable("anime")

        anime?.let {
            // Set anime details in UI
            view.findViewById<TextView>(R.id.anime_title).text = it.title
            view.findViewById<TextView>(R.id.anime_description).text = it.description
            view.findViewById<TextView>(R.id.anime_status).text = it.status

            // Set the anime image using Glide
            val animeImage = view.findViewById<ImageView>(R.id.animeImageView)
            Glide.with(requireContext())
                .load(it.imageUrl)
                .placeholder(R.drawable.placeholder_image) // Show while loading
                // .error(R.drawable.error_image) // Show if loading fails
                .into(animeImage)

            // Set up the "Add to Watchlist" button and its click listener
            val addToWatchlistButton: Button = view.findViewById(R.id.button_add_to_watchlist)
            addToWatchlistButton.setOnClickListener {
                // Directly pass the anime object to the addAnimeToWatchlist function
                addAnimeToWatchlist(anime) // Pass the anime object here (no need for 'it')
            }

            // Set up bottom navigation
            val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_homeFragment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_watchlist -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_watchlistFragment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_Discovery -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_discoveryFragment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_community -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_communityFragment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_events -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_eventsFragment)
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> false
                }
            }
        }
    }

    // Add the anime to the watchlist (this can be a database action)
    private fun addAnimeToWatchlist(anime: Anime) {
        // Check if the user is authenticated
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            Log.d("Auth", "Authenticated User: $userId")

            // Proceed with Firestore operation
            val db = FirebaseFirestore.getInstance()
            val watchlistRef = db.collection("users").document(userId).collection("watchlist")

            // Convert Anime object to a map for Firestore
            val animeMap = hashMapOf(
                "id" to anime.id,
                "title" to anime.title,
                "description" to anime.description,
                "imageUrl" to anime.imageUrl,
                "status" to anime.status
            )

            // Add anime to Firestore
            watchlistRef.document(anime.id.toString()).set(animeMap)
                .addOnSuccessListener {
                    // Show success message when anime is added to Firestore
                    Log.d("Firestore", "${anime.title} added successfully.")
                    Toast.makeText(requireContext(), "${anime.title} added to watchlist", Toast.LENGTH_SHORT).show()

                    // Optionally, disable the button or change its text
                    val addToWatchlistButton: Button = view?.findViewById(R.id.button_add_to_watchlist) ?: return@addOnSuccessListener
                    addToWatchlistButton.text = "Added"
                    addToWatchlistButton.isEnabled = false
                }
                .addOnFailureListener { e ->
                    // Show error message if there’s a problem adding the anime
                    Log.e("Firestore", "Error adding to watchlist: ${e.message}")
                    Toast.makeText(requireContext(), "Error adding anime to watchlist", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where user is not authenticated
            Log.e("Auth", "User is not authenticated.")
            Toast.makeText(requireContext(), "Please sign in to add to the watchlist", Toast.LENGTH_SHORT).show()
        }
    }

}


