// SignUpFragment.kt
package com.example.animelocker // Change to your package name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        userRepository = UserRepository(dbHelper)

        val editTextUsername = view.findViewById<EditText>(R.id.edit_text_username)
        val editTextEmail = view.findViewById<EditText>(R.id.edit_text_email)
        val editTextPassword = view.findViewById<EditText>(R.id.edit_text_password)
        val editTextConfirmPassword = view.findViewById<EditText>(R.id.edit_text_confirm_password)
        val buttonSignUp = view.findViewById<Button>(R.id.button_sign_up)
        val textLoginLink = view.findViewById<TextView>(R.id.loginlink)

        buttonSignUp.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register the user
            if (userRepository.registerUser(username, email, password)) {
                Toast.makeText(requireContext(), "User registered successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signup_to_login)
            } else {
                Toast.makeText(requireContext(), "Username or email already exists", Toast.LENGTH_SHORT).show()
            }
        }

        textLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }
}

