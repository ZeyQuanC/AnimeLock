package com.example.animelocker

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView


    // Add a TAG for logging
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Setup NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup Bottom Navigation
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Log when BottomNavigationView is initialized
        Log.d(TAG, "BottomNavigationView initialized.")

        // Observe fragment changes to show/hide BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Navigated to: ${destination.label}")

            when (destination.id) {
                R.id.homeFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    Log.d(TAG, "Showing BottomNavigationView for HomeFragment.")
                }
                R.id.watchlistFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    Log.d(TAG, "Showing BottomNavigationView for WatchlistFragment.")
                }
                R.id.discoveryFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    Log.d(TAG, "Showing BottomNavigationView for discoveryFragment.")
                }
                R.id.communityFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    Log.d(TAG, "Showing BottomNavigationView for communityFragment.")
                }
                R.id.eventsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    Log.d(TAG, "Showing BottomNavigationView for eventsFragment.")
                }

                else -> {
                    bottomNavigationView.visibility = View.GONE
                    Log.d(TAG, "Hiding BottomNavigationView for destination: ${destination.label}.")
                }
            }
        }

        // Set the OnNavigationItemSelectedListener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            Log.d(TAG, "Menu item selected: ${menuItem.itemId}")
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.homeFragment) // Navigate to Home
                    true
                }
                R.id.navigation_watchlist -> {
                    navController.navigate(R.id.watchlistFragment) // Navigate to Watchlist
                    true
                }
                R.id.navigation_Discovery -> {
                    navController.navigate(R.id.discoveryFragment)
                    true
                }
                R.id.navigation_community -> {
                    navController.navigate(R.id.communityFragment)
                    true
                }
                R.id.navigation_events -> {
                    navController.navigate(R.id.eventsFragment)
                    true
                }
                else -> false
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt user to grant permission manually
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "anime_channel",
                "Anime Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}

