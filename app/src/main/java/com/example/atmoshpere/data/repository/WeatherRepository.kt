package com.example.atmoshpere.data.repository

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.FiveDayForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): CurrentWeatherResponse
    suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String): FiveDayForecastResponse
    
    // Favorites
    fun getAllLocations(): Flow<List<FavoriteLocation>>
    suspend fun insertLocation(location: FavoriteLocation)
    suspend fun deleteLocation(location: FavoriteLocation)

    // Alerts
    fun getAllAlerts(): Flow<List<com.example.atmoshpere.data.local.WeatherAlert>>
    suspend fun insertAlert(alert: com.example.atmoshpere.data.local.WeatherAlert): Long
    suspend fun deleteAlert(alert: com.example.atmoshpere.data.local.WeatherAlert)
    suspend fun updateAlertStatus(id: String, isActive: Boolean)
}
