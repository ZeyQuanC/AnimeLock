package com.example.animelocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
    private lateinit var bioEditText: EditText
    private lateinit var editBioButton: Button
    private lateinit var saveBioButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    private var isEditingBio = false

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


        // Setup the button to toggle between edit mode and display mode for bio
        editBioButton.setOnClickListener {
            toggleBioEditMode(true)
        }

        saveBioButton.setOnClickListener {
            saveBio()
        }

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

        // Fetch user data from Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve user data from Firestore
                        val username = documentSnapshot.getString("username") ?: "Unknown"
                        val bio = documentSnapshot.getString("bio") ?: "No bio available"
                        val profilePicUrl = documentSnapshot.getString("profile_pic_url")

                        // Set text views
                        usernameTextView.text = username
                        bioTextView.text = bio
                        bioEditText.setText(bio)

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
            // Show the EditText and hide the TextView
            bioEditText.visibility = View.VISIBLE
            bioTextView.visibility = View.GONE

            // Change button text to "Save"
            saveBioButton.visibility = View.VISIBLE
            editBioButton.visibility = View.GONE
        } else {
            // Show the TextView and hide the EditText
            bioEditText.visibility = View.GONE
            bioTextView.visibility = View.VISIBLE

            // Change button text to "Edit"
            saveBioButton.visibility = View.GONE
            editBioButton.visibility = View.VISIBLE
        }
    }

    private fun saveBio() {
        val newBio = bioEditText.text.toString()
        if (newBio.isNotEmpty()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(userId)
                    .update("bio", newBio)
                    .addOnSuccessListener {
                        // Update the UI with the new bio
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
}


