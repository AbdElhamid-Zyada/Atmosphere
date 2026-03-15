package com.example.atmoshpere.data.repository

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.local.LocationDao
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.FiveDayForecastResponse
import com.example.atmoshpere.data.remote.WeatherApi
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val dao: LocationDao
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String): CurrentWeatherResponse {
        return api.getCurrentWeather(lat, lon, apiKey, units)
    }

    override suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String): FiveDayForecastResponse {
        return api.getFiveDayForecast(lat, lon, apiKey, units)
    }

    override fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return dao.getAllLocations()
    }

    override suspend fun insertLocation(location: FavoriteLocation) {
        dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FavoriteLocation) {
        dao.deleteLocation(location)
    }

    override fun getAllAlerts(): Flow<List<com.example.atmoshpere.data.local.WeatherAlert>> {
        return dao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: com.example.atmoshpere.data.local.WeatherAlert): Long {
        return dao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: com.example.atmoshpere.data.local.WeatherAlert) {
        dao.deleteAlert(alert)
    }

    override suspend fun updateAlertStatus(id: String, isActive: Boolean) {
        dao.updateAlertStatus(id, isActive)
    }
}
