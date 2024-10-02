// SignUpFragment.kt
package com.example.animelocker

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

            // Register the user with Firebase
            registerUser(username, email, password)
        }

        textLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User registration successful
                    val userId = auth.currentUser?.uid
                    val userData = hashMapOf(
                        "username" to username,
                        "email" to email
                    )

                    // Store user data in Firestore
                    if (userId != null) {
                        firestore.collection("users").document(userId).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "User registered successfully", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error storing user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Registration failed
                    Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

