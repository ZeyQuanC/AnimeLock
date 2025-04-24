package com.example.animelocker

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.google.android.material.button.MaterialButton
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddToWatchlistFragment : Fragment() {

    private lateinit var anime: Anime
    private lateinit var statusDropdown: AutoCompleteTextView
    private lateinit var statusInputLayout: TextInputLayout // Add this for TextInputLayout
    private lateinit var progressSlider: Slider
    private lateinit var progressLabel: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var startDateTextView: TextView
    private lateinit var saveButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_add_to_watchlist, container, false)

        // Initialize views
        statusDropdown = view.findViewById(R.id.status_dropdown)
        statusInputLayout = view.findViewById(R.id.status_dropdown_layout)  // Initialize the TextInputLayout
        progressSlider = view.findViewById(R.id.progress_slider)
        progressLabel = view.findViewById(R.id.progress_label)
        ratingBar = view.findViewById(R.id.rating_bar)
        startDateTextView = view.findViewById(R.id.tv_start_date)
        saveButton = view.findViewById(R.id.button_save)

        // Retrieve passed anime object safely
        arguments?.let {
            anime = it.getParcelable("anime") ?: throw IllegalArgumentException("Anime object is missing")
        }

        // Set up status dropdown (assumes you have predefined statuses)
        val statusList = listOf("Watching", "Completed","Dropped", "Planned")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusList)
        statusDropdown.setAdapter(statusAdapter)

        // Set the status dropdown to show suggestions when clicked
        statusDropdown.setOnClickListener {
            statusDropdown.showDropDown()
        }

        // Hide the label when a choice is selected
        statusDropdown.setOnItemClickListener { _, _, _, _ ->
            // Hide the label using the TextInputLayout's setHintEnabled method
            statusInputLayout.isHintEnabled = false
        }


        // Set default start date to current date
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        startDateTextView.text = currentDate

        // Set up the onClickListener to open the DatePickerDialog
        startDateTextView.setOnClickListener {
            openDatePicker()
        }

        // Set up the progress slider
        progressSlider.addOnChangeListener { _, value, _ ->
            // Update the progress label dynamically as the user moves the slider
            progressLabel.text = String.format(getString(R.string.episodes_watched), value.toInt())
        }

        // Set up save button to add to Firestore
        saveButton.setOnClickListener {
            saveAnimeToWatchlist()
        }

        return view

    }
    private fun openDatePicker() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create and show the DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date to yyyy-MM-dd
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                // Set the selected date to the TextView
                startDateTextView.text = selectedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Function to save the anime to Firestore with the extra data
    private fun saveAnimeToWatchlist() {
        // Check if user is authenticated
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            val watchlistRef = db.collection("users").document(userId).collection("watchlist")

            // Collect data from the UI
            val status = statusDropdown.text.toString()
            val progress = progressSlider.value.toInt()
            val rating = ratingBar.rating.toInt()
            val startDate = startDateTextView.text.toString()

            // Convert anime object to a map to save in Firestore
            val animeMap = hashMapOf(
                "id" to anime.id,
                "title" to anime.title,
                "description" to anime.description,
                "imageUrl" to anime.imageUrl,
                "status" to status,
                "progress" to progress,
                "rating" to rating,
                "startDate" to startDate,
                "endDate" to anime.end_date,
                "mediaType" to anime.media_type,
                "rank" to anime.rank,
                "numEpisodes" to anime.num_episodes
            )


            // Add anime to Firestore
            watchlistRef.document(anime.id.toString()).set(animeMap)
                .addOnSuccessListener {
                    Log.d("Firestore", "${anime.title} added successfully.")
                    Toast.makeText(requireContext(), "${anime.title} added to watchlist", Toast.LENGTH_SHORT).show()

                    // Optionally, navigate back or update UI
                    findNavController().navigateUp() // Navigate back to the previous fragment
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding to watchlist: ${e.message}")
                    Toast.makeText(requireContext(), "Error adding anime to watchlist", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(requireContext(), "Please sign in to add to the watchlist", Toast.LENGTH_SHORT).show()
        }
    }
}


