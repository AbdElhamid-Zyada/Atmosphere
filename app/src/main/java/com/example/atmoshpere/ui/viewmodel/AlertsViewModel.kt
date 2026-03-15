package com.example.atmoshpere.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.data.repository.IWeatherRepository
import com.example.atmoshpere.ui.components.IAlertScheduler
import com.example.atmoshpere.ui.components.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: IWeatherRepository,
    private val alertScheduler: IAlertScheduler,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val alertsList: StateFlow<List<WeatherAlert>> = repository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun scheduleConditionAlert(
        lat: Double, lon: Double, apiKey: String, conditionType: String, threshold: Double,
        label: String, alertType: String, start: Long, end: Long
    ) {
        viewModelScope.launch {
            val alert = alertScheduler.scheduleConditionAlert(lat, lon, apiKey, conditionType, threshold, label, alertType, start, end)
            repository.insertAlert(alert)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            alertScheduler.cancelAlert(alert.id)
            repository.deleteAlert(alert)
        }
    }
}
