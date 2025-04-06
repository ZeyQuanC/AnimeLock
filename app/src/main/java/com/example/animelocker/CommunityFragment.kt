package com.example.animelocker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var discussionAdapter: DiscussionAdapter
    private val discussions = mutableListOf<ForumTopic>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewDiscussions)
        discussionAdapter = DiscussionAdapter(discussions)
        recyclerView.adapter = discussionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadForumTopics()
    }

    private fun loadForumTopics() {
        lifecycleScope.launch {
            try {
                val response = MyAnimeListClient.getApiService().getForumTopics()
                Log.d("API_Response", response.toString()) // Log the response to check data

                discussions.clear()
                discussions.addAll(response.data)
                discussionAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



}

