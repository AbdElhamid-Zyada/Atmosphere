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

    override suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): CurrentWeatherResponse {
        return api.getCurrentWeather(lat, lon, apiKey)
    }

    override suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String): FiveDayForecastResponse {
        return api.getFiveDayForecast(lat, lon, apiKey)
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
}
