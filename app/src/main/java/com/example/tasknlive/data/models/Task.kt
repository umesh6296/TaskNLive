package com.example.tasknlive.data.models

import com.google.firebase.firestore.PropertyName


data class Task(
    val id: String="",
    val title: String,
    val description: String,
    val createdAt: Long,
    @get:PropertyName("completed")
    @set:PropertyName("completed")
    var isCompleted: Boolean=false
)