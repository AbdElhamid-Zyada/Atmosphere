package com.example.atmoshpere.ui.viewmodel

import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.repository.IWeatherRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
    private val repository: IWeatherRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllLocations() } returns flowOf(emptyList())
        viewModel = FavoritesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addLocation_callsInsertFavoriteLocation() = runTest {
        val location = FavoriteLocation(cityName = "Paris", latitude = 48.8, longitude = 2.3)
        coEvery { repository.insertFavoriteLocation(location) } returns Unit

        viewModel.addLocation(location)

        coVerify(exactly = 1) { repository.insertFavoriteLocation(location) }
    }

    @Test
    fun deleteLocation_callsDeleteFavoriteLocation() = runTest {
        val location = FavoriteLocation(id = 3, cityName = "Paris", latitude = 48.8, longitude = 2.3)
        coEvery { repository.deleteFavoriteLocation(location) } returns Unit

        viewModel.deleteLocation(location)

        coVerify(exactly = 1) { repository.deleteFavoriteLocation(location) }
    }

    @Test
    fun favoritesList_exposesFlowFromRepository() = runTest {
        val list = listOf(FavoriteLocation(cityName = "Tokyo", latitude = 35.6, longitude = 139.6))
        coEvery { repository.getAllLocations() } returns flowOf(list)
        
        val newViewModel = FavoritesViewModel(repository)
        
        newViewModel.favoritesList.test {
            assertEquals(list, awaitItem())
        }
    }
}
