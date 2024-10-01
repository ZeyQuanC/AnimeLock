package com.example.animelocker

// DatabaseHelper.kt

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COL_ID = "id"
        private const val COL_USERNAME = "username"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_USERNAME TEXT, $COL_EMAIL TEXT, $COL_PASSWORD TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(username: String, email: String, password: String): Boolean {
        if (isUsernameExists(username) || isEmailExists(email)) {
            return false // Username or email already exists
        }

        val db = this.writableDatabase
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()) // Hash the password
        val contentValues = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, hashedPassword)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()

        return if (result == -1L) {
            Log.d("DatabaseHelper", "User insertion failed")
            false
        } else {
            Log.d("DatabaseHelper", "User inserted successfully with ID: $result")
            true
        }
    }


    private fun isUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME = ?", arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    private fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}
