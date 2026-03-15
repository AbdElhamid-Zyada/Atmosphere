package com.example.atmoshpere.ui.viewmodel

import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.data.repository.IWeatherRepository
import com.example.atmoshpere.ui.components.IAlertScheduler
import com.example.atmoshpere.ui.components.SettingsManager
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
class AlertsViewModelTest {

    private lateinit var viewModel: AlertsViewModel
    private val repository: IWeatherRepository = mockk()
    private val alertScheduler: IAlertScheduler = mockk()
    private val settingsManager: SettingsManager = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { repository.getAllAlerts() } returns flowOf(emptyList())
        coEvery { settingsManager.morningBriefEnabled } returns flowOf(false)
        coEvery { settingsManager.morningBriefHour } returns flowOf(8)
        coEvery { settingsManager.morningBriefMinute } returns flowOf(0)
        
        viewModel = AlertsViewModel(repository, alertScheduler, settingsManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun scheduleConditionAlert_callsInsertAlert() = runTest {
        val alert = WeatherAlert(id = "1", type = "NOTIFICATION", startTime = "08:00", endTime = "18:00")
        coEvery { 
            alertScheduler.scheduleConditionAlert(any(), any(), any(), any(), any(), any(), any(), any(), any()) 
        } returns alert
        coEvery { repository.insertAlert(alert) } returns 1L

        viewModel.scheduleConditionAlert(30.0, 31.0, "key", "RAIN_EXPECTED", 0.0, "Rain Alert", "notification", 1000L, 2000L)

        coVerify(exactly = 1) { repository.insertAlert(alert) }
    }

    @Test
    fun deleteAlert_callsCancelAndDeleteAlert() = runTest {
        val alert = WeatherAlert(id = "5", type = "NOTIFICATION", startTime = "08:00", endTime = "18:00")
        coEvery { alertScheduler.cancelAlert(alert.id) } returns Unit
        coEvery { repository.deleteAlert(alert) } returns Unit

        viewModel.deleteAlert(alert)

        coVerify(exactly = 1) { alertScheduler.cancelAlert(alert.id) }
        coVerify(exactly = 1) { repository.deleteAlert(alert) }
    }

    @Test
    fun alertsList_exposesFlowFromRepository() = runTest {
        val list = listOf(WeatherAlert(id = "1", type = "NOTIFICATION", startTime = "08:00", endTime = "18:00"))
        coEvery { repository.getAllAlerts() } returns flowOf(list)
        
        val newViewModel = AlertsViewModel(repository, alertScheduler, settingsManager)
        
        newViewModel.alertsList.test {
            assertEquals(list, awaitItem())
        }
    }
}
