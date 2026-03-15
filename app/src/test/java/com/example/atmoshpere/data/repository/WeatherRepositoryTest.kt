package com.example.atmoshpere.data.repository

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.local.ILocalDataSource
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.IRemoteDataSource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepositoryImpl
    private val remoteDataSource: IRemoteDataSource = mockk()
    private val localDataSource: ILocalDataSource = mockk()

    @Before
    fun setup() {
        repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun getCurrentWeather_returnsDataFromRemote() = runBlocking {
        val weatherData = mockk<CurrentWeatherResponse>()
        coEvery { remoteDataSource.getCurrentWeather(any(), any(), any(), any()) } returns weatherData

        val result = repository.getCurrentWeather(30.0, 31.0, "key", "metric")

        assertEquals(weatherData, result)
        coVerify(exactly = 1) { remoteDataSource.getCurrentWeather(30.0, 31.0, "key", "metric") }
    }

    @Test
    fun insertFavoriteLocation_delegatesToLocalDataSource() = runBlocking {
        val location = FavoriteLocation(cityName = "London", latitude = 51.5, longitude = -0.1)
        coEvery { localDataSource.insertFavoriteLocation(location) } returns Unit

        repository.insertFavoriteLocation(location)

        coVerify(exactly = 1) { localDataSource.insertFavoriteLocation(location) }
    }
}
