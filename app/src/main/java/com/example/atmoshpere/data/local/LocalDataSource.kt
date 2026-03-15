package com.example.atmoshpere.data.local

import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun insertFavoriteLocation(location: FavoriteLocation)
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    fun getAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(alert: WeatherAlert)

    suspend fun getCachedWeather(): com.example.atmoshpere.data.remote.CurrentWeatherResponse?
    suspend fun insertCachedWeather(weather: com.example.atmoshpere.data.remote.CurrentWeatherResponse)
}

class LocalDataSourceImpl(
    private val favoriteDao: FavoriteLocationDao,
    private val alertDao: AlertDao,
    private val weatherDao: WeatherDao
) : ILocalDataSource {
    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> = favoriteDao.getAllFavoriteLocations()
    override suspend fun insertFavoriteLocation(location: FavoriteLocation) = favoriteDao.insertLocation(location)
    override suspend fun deleteFavoriteLocation(location: FavoriteLocation) = favoriteDao.deleteLocation(location)

    override fun getAlerts(): Flow<List<WeatherAlert>> = alertDao.getAlerts()
    override suspend fun insertAlert(alert: WeatherAlert): Long = alertDao.insertAlert(alert)
    override suspend fun deleteAlert(alert: WeatherAlert) = alertDao.deleteAlert(alert)

    override suspend fun getCachedWeather(): com.example.atmoshpere.data.remote.CurrentWeatherResponse? = weatherDao.getCachedWeather()?.weatherResponse
    override suspend fun insertCachedWeather(weather: com.example.atmoshpere.data.remote.CurrentWeatherResponse) = weatherDao.insertCachedWeather(CachedWeather(weatherResponse = weather))
}
