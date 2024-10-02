package com.example.animelocker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

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

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

            // Check if username and password are not empty
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Authenticate the user
                loginUser(username, password)
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

    private fun loginUser(username: String, password: String) {
        // Query Firestore for the user by username
        firestore.collection("users") // Replace "users" with your actual collection name
            .whereEqualTo("username", username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && task.result.documents.isNotEmpty()) {
                        // User found, retrieve user data
                        val document = task.result.documents[0]
                        val email = document.getString("email") // Assuming you store email with the username
                        Log.d("LoginFragment", "User found: ${document.id} - Email: $email")

                        // Now sign in with Firebase Auth
                        auth.signInWithEmailAndPassword(email ?: "", password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    // Authentication successful, navigate to the home screen
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                } else {
                                    // Show authentication error
                                    Log.e("LoginFragment", "Authentication failed: ${authTask.exception?.message}")
                                    Toast.makeText(requireContext(), "Authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Username not found
                        Log.d("LoginFragment", "Username not found: $username")
                        Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the error
                    Log.e("LoginFragment", "Error getting documents: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
