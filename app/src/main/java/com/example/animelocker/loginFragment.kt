package com.example.animelocker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views by their IDs
        val editTextUsername = view.findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = view.findViewById<Button>(R.id.buttonLogin)
        val signUpLink = view.findViewById<TextView>(R.id.text_sign_up)

        // Set click listener for login button
        buttonLogin.setOnClickListener {
            // Retrieve the username and password entered by the user
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Perform authentication (replace this with your actual authentication logic)
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Authentication successful, navigate to the home screen or perform other actions
                // For now, let's navigate to a placeholder home screen
              /*  findNavController().navigate(R.id.action_loginFragment_to_homeFragment)            */
            } else {
                // Show an error message indicating that username or password is empty
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for sign up link
        signUpLink.setOnClickListener {
            // Navigate to the sign-up fragment
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

    }
}