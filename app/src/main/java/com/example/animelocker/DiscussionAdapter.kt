package com.example.animelocker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DiscussionAdapter(private val discussions: MutableList<ForumTopic>) : RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discussion, parent, false)
        return ViewHolder(view)
    }

    private fun fetchForumTopicContent(id: Int, callback: (String?) -> Unit) {
        // Mock: Replace with your actual network logic (Retrofit/Volley/etc.)
        callback("Here is the detailed content for the forum topic")  // Simulate network call
    }

    private fun toggleTopicExpansion(position: Int) {
        val forumTopic = discussions[position]
        forumTopic.isExpanded = !forumTopic.isExpanded
        Log.d("DiscussionAdapter", "Toggled expansion for topic: ${forumTopic.title}, isExpanded: ${forumTopic.isExpanded}")
        notifyItemChanged(position)  // Notify the item changed to trigger UI update
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forumTopic = discussions[position]

        val authorName = forumTopic.created_by?.name ?: "Unknown"
        val formattedAuthor = holder.itemView.context.getString(R.string.by_author, authorName)

        val rawDate = forumTopic.created_at
        val formattedDate = formatDate(rawDate)

        holder.titleTextView.text = forumTopic.title
        holder.authorTextView.text = formattedAuthor
        holder.dateTextView.text = formattedDate

        // Set up the click listener to toggle expansion
        holder.itemView.setOnClickListener {
            Log.d("DiscussionAdapter", "Clicked on: ${forumTopic.title}")
            toggleTopicExpansion(position)  // Toggle expansion state
        }

        // Toggle visibility based on isExpanded flag
        if (forumTopic.isExpanded) {
            holder.contentTextView.visibility = View.VISIBLE
            // Fetch and display the content
            fetchForumTopicContent(forumTopic.id) { content ->
                holder.contentTextView.text = content ?: "No content available"
            }
        } else {
            holder.contentTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = discussions.size

    private fun formatDate(rawDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(rawDate)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = view.findViewById(R.id.textViewAuthor)
        val dateTextView: TextView = view.findViewById(R.id.textViewDate)
        val contentTextView: TextView = view.findViewById(R.id.textViewContent)
    }
}



