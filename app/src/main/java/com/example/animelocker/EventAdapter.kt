package com.example.animelocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.titleTextView.text = event.title

        // Format the start date
        val startDate = event.startDate
        val formattedDate = "${startDate.year?.toString() ?: "Unknown Year"}-${startDate.month?.toString()?.padStart(2, '0') ?: "Unknown Month"}-${startDate.day?.toString()?.padStart(2, '0') ?: "Unknown Day"}"
        holder.startDateTextView.text = formattedDate


        holder.descriptionText.text = event.description
        holder.descriptionText.visibility = if (event.isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            event.isExpanded = !event.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.eventName)
        val startDateTextView: TextView = itemView.findViewById(R.id.eventStartDate)
        val descriptionText: TextView = itemView.findViewById(R.id.eventDescription)

    }
}



