package com.example.animelocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DiscussionAdapter(private val discussions: List<ForumTopic>) : RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discussion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forumTopic = discussions[position]

        // Safely check if author is null and provide a default value
        val authorName = forumTopic.author?.username ?: "Unknown" // Default to "Unknown" if null
        val formattedAuthor = holder.itemView.context.getString(R.string.by_author, authorName)

        // Format the date

        val rawDate = forumTopic.created_at // Assume this is in ISO 8601 format
        val formattedDate = formatDate(rawDate)


        // Bind the data to the views
        holder.titleTextView.text = forumTopic.title
        holder.authorTextView.text = formattedAuthor // Set the formatted author text
        holder.dateTextView.text = formattedDate // You can format this if needed
    }

    override fun getItemCount(): Int {
        return discussions.size
    }

    private fun formatDate(rawDate: String): String {
        return try {
            // Define the input format (ISO 8601)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set timezone to UTC if needed

            // Parse the raw date string into a Date object
            val date = inputFormat.parse(rawDate)

            // Define the output format (e.g., "MMM dd, yyyy")
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            // Format the Date object into a string
            outputFormat.format(date!!)
        } catch (e: Exception) {
            // In case of error (invalid format or null), return a default date string
            "Invalid Date"
        }
    }

    // ViewHolder class that holds the references to the views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = view.findViewById(R.id.textViewAuthor)
        val dateTextView: TextView = view.findViewById(R.id.textViewDate)
    }
}

