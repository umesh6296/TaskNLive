package com.example.tasknlive.data.models

data class LiveMessage(
    val id: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)