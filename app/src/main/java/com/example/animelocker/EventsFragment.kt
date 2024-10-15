package com.example.animelocker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EventsFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventList: List<Event> // Replace 'Event' with your data class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        // Initialize the CalendarView and RecyclerView
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)

        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        eventList = getEvents() // Method to retrieve events
        eventAdapter = EventAdapter(eventList)
        recyclerView.adapter = eventAdapter

        // Set up the CalendarView listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            updateEventsForSelectedDate(selectedDate)
        }

        return view
    }

    private fun getEvents(): List<Event> {
        // TODO: Retrieve events from your data source
        return listOf() // Placeholder for actual events
    }

    private fun updateEventsForSelectedDate(selectedDate: String) {
        // TODO: Update the event list based on the selected date and notify the adapter
        eventAdapter.notifyDataSetChanged() // Update RecyclerView after fetching new events
    }
}
