package com.example.animelocker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        if (anime == null) {
            // Handle the error or show a default message
            Toast.makeText(context, "Anime data is missing", Toast.LENGTH_SHORT).show()
            return
        }


        anime.let {
            // Set anime details in UI
            view.findViewById<TextView>(R.id.anime_title).text = it.title

            val descriptionText = view.findViewById<TextView>(R.id.anime_description)
            val toggleText = view.findViewById<TextView>(R.id.description_toggle)
            descriptionText.text = it.description ?: "No description available" // Default message if description is null
            var isExpanded = false

            // Toggle logic for expanding/collapsing description
            toggleText.setOnClickListener {
                isExpanded = !isExpanded
                descriptionText.maxLines = if (isExpanded) Int.MAX_VALUE else 4
                toggleText.text = if (isExpanded) getString(R.string.show_less) else getString(R.string.show_more)
            }

            // Set the status, rank, type, episodes, and airing dates
            view.findViewById<TextView>(R.id.anime_status).text = it.status ?: "Unknown"
            view.findViewById<TextView>(R.id.anime_score).text = getString(R.string.score, it.rank?.toString() ?: "N/A") // Using rank
            view.findViewById<TextView>(R.id.anime_type).text = getString(R.string.type, it.media_type ?: "N/A") // Using media_type
            view.findViewById<TextView>(R.id.anime_episodes).text = getString(R.string.episodes, it.num_episodes ?: 0) // Using num_episodes
            view.findViewById<TextView>(R.id.anime_airing_dates).text = getString(R.string.airing_dates, it.start_date ?: "?", it.end_date ?: "?") // Using start_date and end_date

            // Log the anime data for debugging
            Log.d("AnimeDetailFragment", "Start Date: ${it.start_date}")
            Log.d("AnimeDetailFragment", "End Date: ${it.end_date}")
            Log.d("AnimeDetailFragment", "Media Type: ${it.media_type}")
            Log.d("AnimeDetailFragment", "Rank: ${it.rank}")
            Log.d("AnimeDetailFragment", "Status: ${it.status}")
            Log.d("AnimeDetailFragment", "Num Episodes: ${it.num_episodes}")

            // Load the anime image using Glide
            val animeImage = view.findViewById<ImageView>(R.id.animeImageView)
            Glide.with(requireContext())
                .load(it.imageUrl) // Using imageUrl instead of main_picture
                .placeholder(R.drawable.placeholder_image)
                .into(animeImage)

            // Set up "Add to Watchlist" button
            val addToWatchlistButton: Button = view.findViewById(R.id.button_add_to_watchlist)
            addToWatchlistButton.setOnClickListener {
                anime.let { animeItem ->
                    val bundle = Bundle().apply {
                        putParcelable("anime", animeItem) // Pass the Anime object to next fragment
                    }
                    findNavController().navigate(R.id.action_animeDetailFragment_to_addToWatchlistFragment, bundle)
                }
            }



            val reminderButton: Button = view.findViewById(R.id.button_set_reminder)
            reminderButton.setOnClickListener {
                val animeTitle = anime.title ?: "Anime Reminder"
                val content = "Don't forget: a new episode might be airing soon!"

                // TODO: Replace this with a real episode release time if available
                val timeInMillis = System.currentTimeMillis() + 60 * 1000 // For testing: 1 minute from now

                scheduleNotification(animeTitle, content, timeInMillis)
                Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show()
            }


            // Set up bottom navigation
            val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_homeFragment)
                        true
                    }

                    R.id.navigation_watchlist -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_watchlistFragment)
                        true
                    }

                    R.id.navigation_Discovery -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_discoveryFragment)
                        true
                    }

                    R.id.navigation_community -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_communityFragment)
                        true
                    }

                    R.id.navigation_events -> {
                        findNavController().navigate(R.id.action_animeDetailFragment_to_eventsFragment)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun scheduleNotification(title: String, content: String, timeInMillis: Long) {
        val context = requireContext() // Use Fragment's context safely

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("content", content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(permissionIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Permission to set exact alarms is denied", Toast.LENGTH_SHORT).show()
        }
    }





}





