package com.example.animelocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views
        profileImageView = view.findViewById(R.id.profileImageView)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        bioTextView = view.findViewById(R.id.bioTextView)
        bottomNavigationView = view.findViewById(R.id.bottom_navigation)

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.action_ProfileFragment_to_homeFragment)
                    true
                }
                else -> false
            }
        }

        // Get user data from Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve user data, applying default values if necessary
                        val username = documentSnapshot.getString("username") ?: "Unknown"
                        val bio = documentSnapshot.getString("bio") ?: "No bio available"
                        val profilePicUrl = documentSnapshot.getString("profile_pic_url")

                        usernameTextView.text = username
                        bioTextView.text = bio

                        // Load profile picture with Glide, using placeholder if URL is empty
                        if (!profilePicUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(profilePicUrl)
                                .placeholder(R.drawable.default_avatar) // Optional placeholder while loading
                                .into(profileImageView)
                        } else {
                            profileImageView.setImageResource(R.drawable.default_avatar) // Default image if no URL
                        }
                    } else {
                        // Handle case where document doesn't exist
                        Toast.makeText(requireContext(), "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Error getting user data: ${e.message}")
                    // Show error message to the user
                    Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(requireContext(), "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
