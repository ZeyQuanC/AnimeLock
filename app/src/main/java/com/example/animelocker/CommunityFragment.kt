package com.example.animelocker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var discussionAdapter: DiscussionAdapter
    private lateinit var topicFilterSpinner: Spinner
    private val discussions = mutableListOf<ForumTopic>()
    private var selectedTopic: String = "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topicFilterSpinner = view.findViewById(R.id.spinnerTopicFilter)
        recyclerView = view.findViewById(R.id.recyclerViewDiscussions)

        discussionAdapter = DiscussionAdapter(discussions)
        recyclerView.adapter = discussionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupSpinner()
        loadForumTopics()
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


