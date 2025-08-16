package com.example.tasknlive

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.example.tasknlive.util.UserSession

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Firestore offline persistence
        val fs = FirebaseFirestore.getInstance()
        fs.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        // Realtime DB offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // init prefs-backed user id
        UserSession.init(this)
    }
}