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
        // Sample notifications
        notificationList.add(NotificationItem("New Episode Released!", "Attack on Titan has a new episode!"))
        notificationList.add(NotificationItem("Manga Update!", "One Piece Chapter 1100 is out now!"))

        adapter.notifyDataSetChanged()
    }
}
