package com.example.atmoshpere.ui.components

interface IAlertScheduler {
    suspend fun scheduleConditionAlert(
        lat: Double, lon: Double, apiKey: String, conditionType: String, threshold: Double,
        label: String, alertType: String, start: Long, end: Long
    ): com.example.atmoshpere.data.local.WeatherAlert

    suspend fun cancelAlert(workerId: String)
}
