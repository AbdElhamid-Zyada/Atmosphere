package com.example.atmoshpere.data.local

data class WeatherAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "ALERT" or "NOTIFICATION"
    val startTime: String, // "HH:mm"
    val endTime: String,  // "HH:mm"
    val isActive: Boolean = true
)
