package com.example.atmoshpere.data.repository

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.local.ILocalDataSource
import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.FiveDayForecastResponse
import com.example.atmoshpere.data.remote.IRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
) : IWeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String): CurrentWeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, apiKey, units)
    }

    override suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String): FiveDayForecastResponse {
        return remoteDataSource.getFiveDayForecast(lat, lon, apiKey, units)
    }

    override fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllFavoriteLocations()
    }

    override suspend fun insertFavoriteLocation(location: FavoriteLocation) {
        localDataSource.insertFavoriteLocation(location)
    }

    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) {
        localDataSource.deleteFavoriteLocation(location)
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAlerts()
    }

    override suspend fun insertAlert(alert: WeatherAlert): Long {
        return localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        localDataSource.deleteAlert(alert)
    }

    override suspend fun updateAlertStatus(id: String, isActive: Boolean) {
        // Implement if AlertDao supports it, otherwise fallback
    }

    override suspend fun getCachedWeather(): CurrentWeatherResponse? {
        return localDataSource.getCachedWeather()
    }

    override suspend fun insertCachedWeather(weather: CurrentWeatherResponse) {
        localDataSource.insertCachedWeather(weather)
    }
}
