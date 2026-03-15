package com.example.atmoshpere.data.repository

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.FiveDayForecastResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String): CurrentWeatherResponse
    suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String): FiveDayForecastResponse

    fun getAllLocations(): Flow<List<FavoriteLocation>>
    suspend fun insertFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(alert: WeatherAlert)
    suspend fun updateAlertStatus(id: String, isActive: Boolean)

    // Caching support for test scenarios
    suspend fun getCachedWeather(): CurrentWeatherResponse?
    suspend fun insertCachedWeather(weather: CurrentWeatherResponse)
}
