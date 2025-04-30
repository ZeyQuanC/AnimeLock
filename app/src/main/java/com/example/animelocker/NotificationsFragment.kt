package com.example.animelocker


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView



class NotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter
    private val notificationList = mutableListOf<NotificationItem>()
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        recyclerView = view.findViewById(R.id.notificationsRecyclerView)
        bottomNavigationView = view.findViewById(R.id.bottom_navigation)


        // Get the notifications from the parent Activity
        adapter = NotificationsAdapter(notificationList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

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

        loadNotifications() // Load notifications from database or API
        return view


    }

    private fun loadNotifications() {
        // You can later replace this with real API/database data
        notificationList.add(NotificationItem("New Episode Released!", "Attack on Titan has a new episode!"))
        notificationList.add(NotificationItem("Manga Update!", "One Piece Chapter 1100 is out now!"))
        notificationList.add(NotificationItem("Season Finale!", "Jujutsu Kaisen Season 2 finale airs tonight!"))
        notificationList.add(NotificationItem("New Trailer!", "Check out the trailer for Demon Slayer: Infinity Castle Arc!"))
        notificationList.add(NotificationItem("New Anime Added!", "Solo Leveling is now available to watch!"))
        notificationList.add(NotificationItem("Event Reminder!", "Anime Expo Virtual Meetup starts in 2 hours!"))
        notificationList.add(NotificationItem("Manga Update!", "My Hero Academia Chapter 425 is now available!"))
        notificationList.add(NotificationItem("Watch Party!", "Join the community watch party for Naruto this weekend!"))
        notificationList.add(NotificationItem("Cosplay Contest!", "Submit your entry for the Fall Cosplay Challenge!"))
        notificationList.add(NotificationItem("New Feature!", "Offline mode is now live — track your anime anytime!"))
        notificationList.add(NotificationItem("Recommendation!", "Based on your watchlist, try out 'Vinland Saga'!"))
        notificationList.add(NotificationItem("Top Rated!", "Spy x Family is trending in the top 10 this week!"))
        notificationList.add(NotificationItem("New Voice Actor Interview!", "Hear from the cast of Chainsaw Man!"))
        notificationList.add(NotificationItem("Friend Request!", "You have a new friend request from OtakuMaster87!"))
        notificationList.add(NotificationItem("Community Poll!", "Vote now: Best anime opening of the year!"))
        notificationList.add(NotificationItem("New Manga Release!", "Chainsaw Man Part 2 Volume 1 is out!"))
        notificationList.add(NotificationItem("Episode Recap!", "Missed the last episode of Bleach TYBW? Catch up now!"))

        adapter.notifyDataSetChanged()
    }
}
