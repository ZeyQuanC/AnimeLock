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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextUsername = view.findViewById<EditText>(R.id.edit_text_username)
        val editTextEmail = view.findViewById<EditText>(R.id.edit_text_email)
        val editTextPassword = view.findViewById<EditText>(R.id.edit_text_password)
        val editTextConfirmPassword = view.findViewById<EditText>(R.id.edit_text_confirm_password)
        val buttonSignUp = view.findViewById<Button>(R.id.button_sign_up)
        val textLoginLink = view.findViewById<TextView>(R.id.loginlink)

        buttonSignUp.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Make API call to register the user
            registerUser(username, email, password)
        }

        textLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        RetrofitClient.instance.registerUser(username, email, password).enqueue(object : Callback<RegistrationResponse> {
             override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse?.status == "success") {
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                        // Navigate to the login screen or home screen
                        findNavController().navigate(R.id.action_signup_to_login)
                    } else {
                        Toast.makeText(requireContext(), registrationResponse?.message ?: "Error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Server error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Log.e("Signup Error", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
            }

        })
    }

}
