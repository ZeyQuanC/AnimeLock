package com.example.animelocker

import android.os.Bundle
import android.util.Log
import java.text.SimpleDateFormat
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EventsFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private var eventList: List<Event> = emptyList()
    private val eventDates = mutableListOf<Date>()
    private lateinit var recyclerViewStartDates: RecyclerView
    private lateinit var startDateAdapter: StartDateAdapter
    private var startDateList: MutableList<StartDate> = mutableListOf()
    private lateinit var noStartDateMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        noStartDateMessage = view.findViewById(R.id.noStartDateMessage)
        recyclerViewStartDates = view.findViewById(R.id.recyclerViewSecond)

        Log.d("EventsFragment", "onCreateView: CalendarView and RecyclerViews initialized.")

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewStartDates.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewStartDates.adapter = StartDateAdapter(startDateList)

        // Hardcoded events
        val hardcodedEvents = listOf(
            Event(
                "Attack on Titan Final Season",
                AnimeStartDate(2025, 5, 1),
                "Witness the epic conclusion of the legendary anime series that defined a generation."
            ),
            Event(
                "DreamCon 2025",
                AnimeStartDate(2025, 5, 5),
                "A fan convention created by RDCWorld celebrating anime, gaming, and Black creators."
            ),
            Event(
                "ComicCon 2025",
                AnimeStartDate(2025, 5, 10),
                "The biggest pop culture event featuring comics, anime, movies, and celebrity panels."
            ),
            Event(
                "Demon Slayer Season 4 Premiere",
                AnimeStartDate(2025, 5, 12),
                "Premiere of the highly anticipated fourth season of Demon Slayer."
            ),
            Event(
                "My Hero Academia Movie Night",
                AnimeStartDate(2025, 5, 15),
                "A special screening of all My Hero Academia movies with fan activities and trivia."
            ),
            Event(
                "One Piece Celebration Day",
                AnimeStartDate(2025, 5, 18),
                "A celebration of One Piece milestones, including cosplay, quizzes, and live discussions."
            ),
            Event(
                "Crunchyroll Expo 2025",
                AnimeStartDate(2025, 5, 20),
                "Official expo by Crunchyroll featuring exclusive panels, merch, and anime previews."
            ),
            Event(
                "Jujutsu Kaisen Fan Meetup",
                AnimeStartDate(2025, 5, 22),
                "A local meetup for fans of Jujutsu Kaisen to discuss favorite arcs and characters."
            ),
            Event(
                "Anime Expo Lite",
                AnimeStartDate(2025, 5, 24),
                "A digital-friendly, smaller version of Anime Expo with online panels and Q&As."
            ),
            Event(
                "Fullmetal Alchemist 20th Anniversary",
                AnimeStartDate(2025, 5, 26),
                "Commemorating 20 years of Fullmetal Alchemist with screenings and special guests."
            ),
            Event(
                "Naruto Tribute Marathon",
                AnimeStartDate(2025, 5, 27),
                "An all-day streaming marathon celebrating Naruto’s legacy."
            ),
            Event(
                "Spy x Family Season 3 Teaser",
                AnimeStartDate(2025, 5, 28),
                "Exclusive teaser reveal and behind-the-scenes footage of Season 3 of Spy x Family."
            ),
            Event(
                "Bleach Thousand-Year Blood War Finale",
                AnimeStartDate(2025, 5, 29),
                "Final episode screening of the Bleach TYBW arc with fanfare and discussions."
            ),
            Event(
                "Chainsaw Man Live Panel",
                AnimeStartDate(2025, 5, 30),
                "Live panel with the Chainsaw Man creators and voice cast, plus Q&A session."
            ),
            Event(
                "Anime Music Festival",
                AnimeStartDate(2025, 5, 31),
                "A concert celebrating anime music featuring live performances from top artists."
            )
        )


        // Launch coroutine to fetch API events and combine them
        CoroutineScope(Dispatchers.Main).launch {
            val apiEvents = getEvents()
            val combinedEvents = hardcodedEvents + apiEvents
            eventList = combinedEvents

            eventAdapter = EventAdapter(combinedEvents)
            recyclerView.adapter = eventAdapter

            Log.d("EventsFragment", "Combined events: $combinedEvents")
        }

        // Initialize adapter to avoid nulls before coroutine completes
        eventAdapter = EventAdapter(eventList)
        recyclerView.adapter = eventAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Log.d("EventsFragment", "CalendarView: Selected date - $selectedDate")
            updateEventsForSelectedDate(selectedDate)
        }

        fetchAnimeStartDates()

        return view
    }

    private fun fetchAnimeStartDates() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val watchlistRef = db.collection("users").document(userId).collection("watchlist")

            watchlistRef.get()
                .addOnSuccessListener { documents ->
                    val startDates = mutableListOf<StartDate>()
                    for (document in documents) {
                        val startDate = document.getString("startDate")
                        val title = document.getString("title")
                        startDate?.let {
                            title?.let {
                                startDates.add(StartDate(title = it, date = startDate))
                            }
                        }
                    }

                    if (startDates.isEmpty()) {
                        noStartDateMessage.visibility = View.VISIBLE
                        recyclerViewStartDates.visibility = View.GONE
                    } else {
                        noStartDateMessage.visibility = View.GONE
                        recyclerViewStartDates.visibility = View.VISIBLE
                        startDateAdapter = StartDateAdapter(startDates)
                        recyclerViewStartDates.adapter = startDateAdapter
                    }

                    Log.d("EventsFragment", "Fetched start dates and titles: $startDates")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error getting documents: ", e)
                }
        } else {
            Log.d("EventsFragment", "User not logged in.")
        }
    }

    private fun addDateToCalendar(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendarView.setDate(calendar.timeInMillis, true, true)
    }

    private suspend fun getEvents(): List<Event> {
        val query = """
        query {
            Page(page: 1, perPage: 20) {
                media {
                    title {
                        romaji
                    }
                    startDate {
                        year
                        month
                        day
                    }
                }
            }
        }
        """

        val queryMap = mapOf("query" to query)

        return try {
            val response = RetrofitInstance.api.getAnimeEvents(queryMap)

            if (response.isSuccessful) {
                val mediaList = response.body()?.data?.Page?.media
                val events = mediaList?.mapNotNull { media ->
                    val title = media.title.romaji ?: "Unknown"
                    val startDate = media.startDate?.let {
                        AnimeStartDate(it.year, it.month, it.day)
                    } ?: AnimeStartDate(null, null, null)

                    val description = "No description available"


                    if (title.isNotEmpty() && startDate.year != null && description.isNotEmpty()) {
                        Event(title = title, startDate = startDate, description = description)
                    }
                    else null
                } ?: emptyList()

                withContext(Dispatchers.Main) {
                    eventAdapter = EventAdapter(events)
                    recyclerView.adapter = eventAdapter
                }

                events
            } else {
                Log.e("AniList", "Error fetching events: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("AniList", "Network error: $e")
            emptyList()
        }
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())  // Input format: "1/5/2025"
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Desired format: "2025-05-01"

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())  // Convert to the desired format
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    private fun updateEventsForSelectedDate(selectedDate: String) {
        // Format the selected date to match the format of event dates (yyyy-MM-dd)
        val formattedSelectedDate = formatDate(selectedDate)

        Log.d("EventsFragment", "Filtering events for selected date: $formattedSelectedDate")

        val filteredEvents = eventList.filter {
            val eventDateFormatted = it.startDate.toFormattedString()

            Log.d("EventsFragment", "Event: ${it.title}, Event Date: $eventDateFormatted, Selected Date: $formattedSelectedDate")

            // Compare formatted dates
            val isMatch = eventDateFormatted == formattedSelectedDate

            Log.d("EventsFragment", "Match result: $isMatch")
            isMatch
        }

        if (filteredEvents.isEmpty()) {
            Log.d("EventsFragment", "No events found for selected date.")
            noStartDateMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            Log.d("EventsFragment", "Found ${filteredEvents.size} events for selected date.")
            noStartDateMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            eventAdapter.updateEvents(filteredEvents)
        }

        eventAdapter.notifyDataSetChanged()
    }




}



