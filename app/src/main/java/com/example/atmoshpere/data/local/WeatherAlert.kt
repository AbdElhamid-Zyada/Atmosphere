package com.example.atmoshpere.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "ALERT" or "NOTIFICATION"
    val startTime: String, // "HH:mm"
    val endTime: String,  // "HH:mm"
    val isActive: Boolean = true
)
