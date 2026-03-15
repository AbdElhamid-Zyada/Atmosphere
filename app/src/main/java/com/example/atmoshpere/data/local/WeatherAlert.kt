package com.example.atmoshpere.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean = true
)
