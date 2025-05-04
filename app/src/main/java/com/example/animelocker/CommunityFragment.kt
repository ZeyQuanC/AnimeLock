package com.example.animelocker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var discussionAdapter: DiscussionAdapter
    private lateinit var topicFilterSpinner: Spinner
    private lateinit var postEditText: EditText
    private lateinit var postButton: Button
    private val discussions = mutableListOf<ForumTopic>()
    private var selectedTopic: String = "all"
    private val db = FirebaseFirestore.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topicFilterSpinner = view.findViewById(R.id.spinnerTopicFilter)
        recyclerView = view.findViewById(R.id.recyclerViewDiscussions)
        postEditText = view.findViewById(R.id.editTextDiscussion)
        postButton = view.findViewById(R.id.buttonPost)


        discussionAdapter = DiscussionAdapter(discussions)
        recyclerView.adapter = discussionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        postButton.setOnClickListener {
            val content = postEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                postNewDiscussion(content)
            } else {
                Toast.makeText(requireContext(), "Please enter a discussion topic.", Toast.LENGTH_SHORT).show()
            }
        }


        setupSpinner()
        loadForumTopics()
    }

    // Format the current timestamp to follow the ISO 8601 format
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // Use UTC timezone for consistency
        return sdf.format(Date())
    }



    private fun postNewDiscussion(content: String) {
        val newTopic = ForumTopic(
            id = System.currentTimeMillis().toInt(), // Simulated unique ID
            title = content,
            created_at = getCurrentTimestamp(), // Use ISO 8601 format for created_at
            num_posts = 0,
            created_by = Author(id = 0, name = "You"), // Placeholder for current user
            url = null,
            content = content,
            isExpanded = false,
            body = content
        )


        saveToFirestore(newTopic)

        // Log detailed information
        Log.d("Posted_Topic", "New topic posted: $newTopic")
        Log.d("Posted_Topic", "Created At: ${newTopic.created_at}")  // Will now show in ISO 8601 format

        discussions.add(0, newTopic)
        discussionAdapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        postEditText.text.clear()
    }

    // Function to save the ForumTopic to Firestore
    private fun saveToFirestore(forumTopic: ForumTopic) {
        val topicData = hashMapOf(
            "title" to forumTopic.title,
            "created_at" to forumTopic.created_at,
            "num_posts" to forumTopic.num_posts,
            "created_by" to forumTopic.created_by?.name,
            "url" to forumTopic.url,
            "content" to forumTopic.content,
            "body" to forumTopic.body,
            "isExpanded" to forumTopic.isExpanded,
            "timestamp" to FieldValue.serverTimestamp() // Automatically set the timestamp in Firestore
        )

        val db = FirebaseFirestore.getInstance()


        db.collection("forum_topics")
            .add(topicData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
        Log.d("Auth", "User is not authenticated.")

    }


    private fun setupSpinner() {
        val topics = listOf("All", "Anime", "Episode", "Character", "Manga")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, topics)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        topicFilterSpinner.adapter = adapter

        topicFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTopic = when (topics[position]) {
                    "Anime" -> "anime"
                    "Episode" -> "episode"
                    "Character" -> "character"
                    "Manga" -> "manga"
                    else -> "all"
                }
                loadForumTopics()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadForumTopics() {
        lifecycleScope.launch {
            try {
                val query = if (selectedTopic == "all") "" else selectedTopic
                val response = MyAnimeListClient.getApiService().getForumTopics(query)
                Log.d("API_Response", response.toString())

                discussions.clear()
                discussions.addAll(response.data)
                discussionAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



}


