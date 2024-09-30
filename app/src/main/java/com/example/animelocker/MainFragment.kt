package com.example.animelocker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views by their IDs
        val loginButton = view.findViewById<Button>(R.id.button_login)
        val signUpButton = view.findViewById<Button>(R.id.button_signup)


        // Handle button clicks
        loginButton.setOnClickListener {
            // Navigate to the login screen using the Navigation component
            findNavController().navigate(R.id.action_main_to_login)
        }

        signUpButton.setOnClickListener {
            // Navigate to the sign-up screen using the Navigation component
            findNavController().navigate(R.id.action_main_to_signup)
        }


    }


}