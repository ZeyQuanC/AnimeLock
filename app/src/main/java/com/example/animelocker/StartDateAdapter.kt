package com.example.animelocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
            startDateTextView.text = startDate.date // Set the start date
        }
    }
}




