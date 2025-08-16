package com.example.tasknlive.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}