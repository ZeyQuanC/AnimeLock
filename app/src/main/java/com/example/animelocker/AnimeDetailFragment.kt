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

            val addToWatchlistButton: Button = view.findViewById(R.id.button_add_to_watchlist)
            addToWatchlistButton.setOnClickListener {
                anime.let {
                    // Create a bundle and put the Anime object in it
                    val bundle = Bundle().apply {
                        putParcelable("anime", it)  // Put the anime object as a Parcelable in the bundle
                    }

                    // Navigate to AddToWatchlistFragment with the bundle
                    findNavController().navigate(R.id.action_animeDetailFragment_to_addToWatchlistFragment, bundle)
                }
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


}


