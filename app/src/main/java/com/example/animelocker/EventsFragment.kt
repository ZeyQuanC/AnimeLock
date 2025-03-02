package com.example.animelocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EventsFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventList: List<Event> // Replace 'Event' with your data class
    private val eventDates = mutableListOf<Date>() // To store dates for the calendar
    private lateinit var recyclerViewStartDates: RecyclerView
    private lateinit var startDateAdapter: StartDateAdapter
    private var startDateList: MutableList<StartDate> = mutableListOf()
    private lateinit var noStartDateMessage: TextView // TextView to display "No anime started on this date"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        // Initialize the CalendarView and RecyclerView
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        noStartDateMessage = view.findViewById(R.id.noStartDateMessage)
        recyclerViewStartDates = view.findViewById(R.id.recyclerViewSecond)

        Log.d("EventsFragment", "onCreateView: CalendarView and RecyclerViews initialized.")

        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewStartDates.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewStartDates.adapter = StartDateAdapter(startDateList)
        eventList = getEvents() // Method to retrieve events
        eventAdapter = EventAdapter(eventList)
        recyclerView.adapter = eventAdapter

        Log.d("EventsFragment", "onCreateView: RecyclerView set with eventAdapter.")

        // Set up the CalendarView listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Log.d("EventsFragment", "CalendarView: Selected date - $selectedDate")
            updateEventsForSelectedDate(selectedDate)
        }

        // Call the fetchAnimeStartDates function to fetch and display anime start dates
        Log.d("EventsFragment", "onCreateView: Calling fetchAnimeStartDates to retrieve start dates.")
        fetchAnimeStartDates()

        return view
    }

    private fun fetchAnimeStartDates() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.d("EventsFragment", "fetchAnimeStartDates: Fetching start dates for user: $userId")
            val db = FirebaseFirestore.getInstance()
            val watchlistRef = db.collection("users").document(userId).collection("watchlist")

            watchlistRef.get()
                .addOnSuccessListener { documents ->
                    val startDates = mutableListOf<StartDate>() // List to store StartDate objects
                    for (document in documents) {
                        // Assuming you have "startDate" and "title" fields in the Firestore document
                        val startDate = document.getString("startDate")
                        val title = document.getString("title") // Fetch title from Firestore
                        startDate?.let {
                            title?.let {
                                startDates.add(StartDate(title = it, date = startDate)) // Add StartDate object to the list
                            }
                        }
                    }
                    Log.d("EventsFragment", "fetchAnimeStartDates: Retrieved start dates and titles: $startDates")

                    if (startDates.isEmpty()) {
                        Log.d("EventsFragment", "fetchAnimeStartDates: No start dates found.")
                        noStartDateMessage.visibility = View.VISIBLE
                        recyclerViewStartDates.visibility = View.GONE
                    } else {
                        Log.d("EventsFragment", "fetchAnimeStartDates: Found start dates, updating UI.")
                        noStartDateMessage.visibility = View.GONE
                        recyclerViewStartDates.visibility = View.VISIBLE

                        // Pass the list of StartDate objects to your adapter
                        startDateAdapter = StartDateAdapter(startDates)
                        recyclerViewStartDates.adapter = startDateAdapter
                    }
                    Log.d("EventsFragment", "Fetched start dates and titles: $startDates")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "fetchAnimeStartDates: Error getting documents: ", e)
                }
        } else {
            Log.d("EventsFragment", "fetchAnimeStartDates: User not logged in.")
        }
    }



    // Add the date to the CalendarView
    private fun addDateToCalendar(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date

        Log.d("EventsFragment", "addDateToCalendar: Adding date to calendar - $date")

        // Mark the specific date on the calendar
        calendarView.setDate(calendar.timeInMillis, true, true)
    }

    // Placeholder to fetch events from your data source
    private fun getEvents(): List<Event> {
        // TODO: Retrieve events from your data source
        Log.d("EventsFragment", "getEvents: Retrieving events (Placeholder).")
        return listOf() // Placeholder for actual events
    }

    // Filter events based on selected date
    private fun updateEventsForSelectedDate(selectedDate: String) {
        Log.d("EventsFragment", "updateEventsForSelectedDate: Filtering events for selected date: $selectedDate")
        // TODO: Filter events from your data source based on the selected date
        eventAdapter.notifyDataSetChanged() // Update RecyclerView after fetching new events
    }
}


