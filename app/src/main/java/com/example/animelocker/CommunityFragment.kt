package com.example.animelocker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var discussionAdapter: DiscussionAdapter
    private lateinit var discussions: MutableList<Discussion>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewDiscussions)
        discussions = mutableListOf()
        discussionAdapter = DiscussionAdapter(discussions)

        recyclerView.adapter = discussionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.buttonPost).setOnClickListener {
            postDiscussion()
        }

        // Load discussions from Firestore
        loadDiscussions()
    }

    private fun postDiscussion() {
        // Safely access the EditText
        val editTextDiscussion = view?.findViewById<EditText>(R.id.editTextDiscussion)

        // Check if the EditText is not null and retrieve the message
        val message = editTextDiscussion?.text.toString()

        if (message.isNotEmpty()) {
            val discussion = Discussion(username = "User", message = message)

            // Add the discussion to Firestore
            firestore.collection("discussions").add(discussion)
                .addOnSuccessListener {
                    // Clear the EditText after successfully posting
                    editTextDiscussion?.text?.clear() // Use safe call here
                    loadDiscussions() // Reload discussions after posting
                }
                .addOnFailureListener { e ->
                    // Show error message if posting fails
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Optionally handle the case when the message is empty
            Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadDiscussions() {
        firestore.collection("discussions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                discussions.clear()
                snapshot?.forEach { doc ->
                    discussions.add(doc.toObject(Discussion::class.java))
                }
                discussionAdapter.notifyDataSetChanged()
            }
    }
}
