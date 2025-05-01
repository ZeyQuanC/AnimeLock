package com.example.animelocker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class StartDateAdapter(private val startDates: MutableList<StartDate>) : RecyclerView.Adapter<StartDateAdapter.StartDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_start_date, parent, false)
        return StartDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: StartDateViewHolder, position: Int) {
        val startDate = startDates[position]
        holder.bind(startDate)
    }

    override fun getItemCount(): Int {
        return startDates.size
    }

    inner class StartDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val startDateTextView: TextView = itemView.findViewById(R.id.startDateTextView)

        fun bind(startDate: StartDate) {
            titleTextView.text = startDate.title // Set the title of the show

            // Format the start date
            val formattedDate = try {
                // Assume the date format in the StartDate is "yyyy-MM-dd"
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val date = inputDateFormat.parse(startDate.date)
                if (date != null) {
                    val formatted = outputDateFormat.format(date)
                    Log.d("StartDateAdapter", "Formatted date: $formatted") // Log formatted date
                    formatted
                } else {
                    Log.d("StartDateAdapter", "Invalid date: ${startDate.date}") // Log if date is invalid
                    startDate.date // Return original if invalid
                }
            } catch (e: ParseException) {
                Log.e("StartDateAdapter", "Error parsing date: ${startDate.date}", e) // Log parse error
                startDate.date // Return original if parsing fails
            }

            startDateTextView.text = formattedDate.toString() // Set the formatted start date
        }
    }
}





