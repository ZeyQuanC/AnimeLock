package com.example.animelocker

// UserRepository.kt

class UserRepository(private val dbHelper: DatabaseHelper) {

    fun registerUser(username: String, email: String, password: String): Boolean {
        return dbHelper.addUser(username, email, password)
    }

    // Additional methods can be added here for future functionality
}
