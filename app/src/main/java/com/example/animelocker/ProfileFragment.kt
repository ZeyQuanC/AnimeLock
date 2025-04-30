package com.example.animelocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import android.text.TextUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var bioEditText: EditText
    private lateinit var editBioButton: Button
    private lateinit var saveBioButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bioToggleTextView: TextView

    private var isEditingBio = false
    private var isBioExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views
        profileImageView = view.findViewById(R.id.profileImageButton)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        bioTextView = view.findViewById(R.id.bioTextView)
        bioEditText = view.findViewById(R.id.bioEditText)
        editBioButton = view.findViewById(R.id.editBioButton)
        saveBioButton = view.findViewById(R.id.saveBioButton)
        bottomNavigationView = view.findViewById(R.id.bottom_navigation)
        bioToggleTextView = view.findViewById(R.id.bio_toggle)

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.homeFragment) // Navigate to Home
                    true
                }
                R.id.navigation_watchlist -> {
                    findNavController().navigate(R.id.watchlistFragment) // Navigate to Watchlist
                    true
                }
                R.id.navigation_Discovery -> {
                    findNavController().navigate(R.id.discoveryFragment)
                    true
                }
                R.id.navigation_community -> {
                    findNavController().navigate(R.id.communityFragment)
                    true
                }
                R.id.navigation_events -> {
                    findNavController().navigate(R.id.eventsFragment)
                    true
                }
                else -> false
            }
        }


        val completedAnimeRecyclerView = view.findViewById<RecyclerView>(R.id.completedAnimeRecyclerView)
        completedAnimeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        loadCompletedAnime()


        // Toggle bio edit
        editBioButton.setOnClickListener { toggleBioEditMode(true) }
        saveBioButton.setOnClickListener { saveBio() }

        // Fetch user data
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val username = documentSnapshot.getString("username") ?: "Unknown"
                        val bio = documentSnapshot.getString("bio") ?: "No bio available"
                        val profilePicUrl = documentSnapshot.getString("profile_pic_url")

                        usernameTextView.text = username
                        bioTextView.text = bio
                        bioEditText.setText(bio)

                        // Initial bio display
                        bioTextView.maxLines = 3
                        bioTextView.ellipsize = TextUtils.TruncateAt.END
                        bioToggleTextView.text = getString(R.string.show_more)

                        // Hide toggle if bio is short
                        bioToggleTextView.visibility = if (bio.length < 100) View.GONE else View.VISIBLE

                        bioToggleTextView.setOnClickListener {
                            isBioExpanded = !isBioExpanded
                            bioTextView.maxLines = if (isBioExpanded) Int.MAX_VALUE else 3
                            bioTextView.ellipsize = if (isBioExpanded) null else TextUtils.TruncateAt.END
                            bioToggleTextView.text = if (isBioExpanded)
                                getString(R.string.show_less) else getString(R.string.show_more)
                        }

                        // Load profile picture
                        if (!profilePicUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(profilePicUrl)
                                .placeholder(R.drawable.default_avatar)
                                .into(profileImageView)
                        } else {
                            profileImageView.setImageResource(R.drawable.default_avatar)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Error getting user data: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun toggleBioEditMode(isEditing: Boolean) {
        isEditingBio = isEditing
        if (isEditing) {
            bioEditText.visibility = View.VISIBLE
            bioTextView.visibility = View.GONE
            bioToggleTextView.visibility = View.GONE
            saveBioButton.visibility = View.VISIBLE
            editBioButton.visibility = View.GONE
        } else {
            bioEditText.visibility = View.GONE
            bioTextView.visibility = View.VISIBLE
            saveBioButton.visibility = View.GONE
            editBioButton.visibility = View.VISIBLE

            // Re-check if toggle is needed
            val updatedBio = bioEditText.text.toString()
            bioToggleTextView.visibility = if (updatedBio.length < 100) View.GONE else View.VISIBLE
        }
    }

    private fun saveBio() {
        val newBio = bioEditText.text.toString()
        if (newBio.isNotEmpty()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("users").document(userId)
                    .update("bio", newBio)
                    .addOnSuccessListener {
                        bioTextView.text = newBio
                        toggleBioEditMode(false)
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileFragment", "Error saving bio: ${e.message}")
                        Toast.makeText(requireContext(), "Failed to save bio", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Bio cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadCompletedAnime() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.w("CompletedAnime", "User is not logged in or UID is null")
            return
        }

        val watchlistRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("watchlist")

        watchlistRef.whereEqualTo("status", "Completed") // only completed entries
            .get()
            .addOnSuccessListener { documents ->
                val completedAnimeList = mutableListOf<Anime>()
                for (doc in documents) {
                    val anime = doc.toObject(Anime::class.java)
                    completedAnimeList.add(anime)
                }

                updateCompletedAnimeRecyclerView(completedAnimeList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch completed anime", e)
            }
    }




    private fun updateCompletedAnimeRecyclerView(completedAnimeList: List<Anime>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.completedAnimeRecyclerView)

        if (recyclerView == null) {
            Log.w("CompletedAnime", "RecyclerView not found!")
            return
        }

        val adapter = AnimeAdapter(
            completedAnimeList,
            onAnimeClick = { anime ->
                Log.d("CompletedAnimeClick", "Clicked on ${anime.title}")
            },
            onAnimeLongClick = { anime ->
                Log.d("CompletedAnimeLongClick", "Long-clicked on ${anime.title}")
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        Log.d("CompletedAnime", "RecyclerView updated with ${completedAnimeList.size} items")
    }

}




