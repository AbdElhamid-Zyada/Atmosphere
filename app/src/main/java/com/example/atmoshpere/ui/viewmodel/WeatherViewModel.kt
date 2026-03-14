package com.example.atmoshpere.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmoshpere.BuildConfig
import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.data.remote.ApiClient
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.example.atmoshpere.data.remote.FiveDayForecastResponse
import com.example.atmoshpere.data.receiver.WeatherAlertReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentWeather = MutableStateFlow<CurrentWeatherResponse?>(null)
    val currentWeather: StateFlow<CurrentWeatherResponse?> = _currentWeather

    private val _forecast = MutableStateFlow<FiveDayForecastResponse?>(null)
    val forecast: StateFlow<FiveDayForecastResponse?> = _forecast

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Alarms/Alerts State
    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    // Favorites State
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites: StateFlow<List<FavoriteLocation>> = _favorites

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val locationDao = com.example.atmoshpere.data.local.AppDatabase.getDatabase(application).locationDao()

    init {
        android.util.Log.d("AtmosphereDebug", "ViewModel Init Started")
        // Load initial dummy alerts or from db
        _alerts.value = listOf(
            WeatherAlert(type = "ALERT", startTime = "08:00", endTime = "14:00"),
            WeatherAlert(type = "NOTIFICATION", startTime = "10:00", endTime = "18:00")
        )

        // Load Favorites from DB
        viewModelScope.launch {
            locationDao.getAllLocations().collect {
                _favorites.value = it
            }
        }
    }

    fun addLocation(name: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            locationDao.insertLocation(FavoriteLocation(cityName = name, latitude = lat, longitude = lon))
        }
    }

    fun removeLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.deleteLocation(location)
        }
    }

    fun fetchLocationAndWeather() {
        android.util.Log.d("AtmosphereDebug", "fetchLocationAndWeather Called")
        // Static fetch for current location for now
        fetchWeather(51.5074, -0.1278) // London
    }

    fun fetchWeather(lat: Double, lon: Double) {
        android.util.Log.d("AtmosphereDebug", "fetchWeather Called for $lat, $lon")
        viewModelScope.launch {
            _isRefreshing.value = true
            
            val currentJob = launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    android.util.Log.d("AtmosphereDebug", "Current Call started")
                    val current = ApiClient.weatherApi.getCurrentWeather(lat, lon, BuildConfig.API_KEY)
                    _currentWeather.value = current
                    android.util.Log.d("AtmosphereDebug", "Current weather saved")
                } catch (e: Exception) {
                    android.util.Log.e("AtmosphereDebug", "Current API failed: ${e.message}", e)
                }
            }

            /*
            val forecastJob = launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    android.util.Log.d("AtmosphereDebug", "Forecast Call started")
                    val forecastData = ApiClient.weatherApi.getFiveDayForecast(lat, lon, BuildConfig.API_KEY)
                    _forecast.value = forecastData
                    android.util.Log.d("AtmosphereDebug", "Forecast saved")
                } catch (e: Exception) {
                    android.util.Log.e("AtmosphereDebug", "Forecast API failed: ${e.message}", e)
                }
            }
            */

            currentJob.join()
            // forecastJob.join()
            
            _isRefreshing.value = false
            android.util.Log.d("AtmosphereDebug", "fetchWeather Completed")
        }
    }

    // --- ALERTS CRUD & SCHEDULING ---

    fun addAlert(alert: WeatherAlert) {
        val updated = _alerts.value + alert
        _alerts.value = updated
        if (alert.isActive) scheduleAlarm(alert)
    }

    fun removeAlert(alert: WeatherAlert) {
        val updated = _alerts.value.filter { it.id != alert.id }
        _alerts.value = updated
        cancelAlarm(alert)
    }

    fun toggleAlert(alert: WeatherAlert) {
        val updatedlist = _alerts.value.map {
            if (it.id == alert.id) it.copy(isActive = !it.isActive) else it
        }
        _alerts.value = updatedlist
        val newTarget = updatedlist.first { it.id == alert.id }
        if (newTarget.isActive) scheduleAlarm(newTarget) else cancelAlarm(newTarget)
    }

    fun updateAlert(oldAlert: WeatherAlert, newAlert: WeatherAlert) {
        cancelAlarm(oldAlert)
        val updatedlist = _alerts.value.map { if (it.id == oldAlert.id) newAlert else it }
        _alerts.value = updatedlist
        if (newAlert.isActive) scheduleAlarm(newAlert)
    }

    private fun scheduleAlarm(alert: WeatherAlert) {
        val intent = Intent(getApplication(), WeatherAlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
            putExtra("ALERT_TYPE", alert.type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val parts = alert.startTime.split(":")
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
               add(Calendar.DATE, 1) // Trigger tomorrow instead if past
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("WeatherViewModel", "SecurityException scheduling alarm: ${e.message}")
        }
    }

    private fun cancelAlarm(alert: WeatherAlert) {
        val intent = Intent(getApplication(), WeatherAlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
