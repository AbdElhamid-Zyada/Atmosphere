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
}
