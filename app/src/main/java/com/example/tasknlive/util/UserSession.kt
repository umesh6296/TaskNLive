package com.example.tasknlive.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

object UserSession {
    private const val PREF_NAME = "tasknlive_prefs"
    private const val KEY_USERNAME = "username"
    private lateinit var prefs: SharedPreferences

    val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: "Guest"
    }
}