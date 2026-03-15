package com.example.atmoshpere.data.local

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSourceImpl
    private val favoriteDao: FavoriteLocationDao = mockk()
    private val alertDao: AlertDao = mockk()
    private val weatherDao: WeatherDao = mockk()

    @Before
    fun setup() {
        localDataSource = LocalDataSourceImpl(favoriteDao, alertDao, weatherDao)
    }

    @Test
    fun insertFavoriteLocation_delegatesToFavoriteDao() = runBlocking {
        val location = FavoriteLocation(cityName = "Alexandria", latitude = 31.2, longitude = 29.9)
        coEvery { favoriteDao.insertLocation(location) } returns Unit

        localDataSource.insertFavoriteLocation(location)

        coVerify(exactly = 1) { favoriteDao.insertLocation(location) }
    }

    @Test
    fun deleteFavoriteLocation_delegatesToFavoriteDao() = runBlocking {
        val location = FavoriteLocation(id = 2, cityName = "Alexandria", latitude = 31.2, longitude = 29.9)
        coEvery { favoriteDao.deleteLocation(location) } returns Unit

        localDataSource.deleteFavoriteLocation(location)

        coVerify(exactly = 1) { favoriteDao.deleteLocation(location) }
    }

    @Test
    fun getCachedWeather_returnsWeatherResponseFromDao() = runBlocking {
        val weatherResponse = mockk<com.example.atmoshpere.data.remote.CurrentWeatherResponse>()
        val cachedWeather = CachedWeather(id = 1, weatherResponse = weatherResponse)
        coEvery { weatherDao.getCachedWeather() } returns cachedWeather

        val result = localDataSource.getCachedWeather()

        assertEquals(weatherResponse, result)
        coVerify(exactly = 1) { weatherDao.getCachedWeather() }
    }
}
