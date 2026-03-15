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
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _uiEvent = kotlinx.coroutines.channels.Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Autocomplete Solutions
    private val _autocompleteResults = MutableStateFlow<List<com.example.atmoshpere.data.remote.GeocodingResponse>>(emptyList())
    val autocompleteResults: StateFlow<List<com.example.atmoshpere.data.remote.GeocodingResponse>> = _autocompleteResults

    private val _reverseGeocodeResult = MutableStateFlow<com.example.atmoshpere.data.remote.GeocodingResponse?>(null)
    val reverseGeocodeResult: StateFlow<com.example.atmoshpere.data.remote.GeocodingResponse?> = _reverseGeocodeResult

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(application)
    
    private val repository: com.example.atmoshpere.data.repository.WeatherRepository = com.example.atmoshpere.data.repository.WeatherRepositoryImpl(
        api = com.example.atmoshpere.data.remote.ApiClient.weatherApi,
        dao = com.example.atmoshpere.data.local.AppDatabase.getDatabase(application).locationDao()
    )

    init {
        android.util.Log.d("AtmosphereDebug", "ViewModel Init Started")

        // Load Alerts from DB
        viewModelScope.launch {
            repository.getAllAlerts().collect {
                _alerts.value = it
            }
        }

        // Load Favorites from DB
        viewModelScope.launch {
            repository.getAllLocations().collect {
                _favorites.value = it
            }
        }
    }

    fun addLocation(name: String, lat: Double, lon: Double, country: String) {
        viewModelScope.launch {
            try {
                // Fetch to get exact timezone offset from OpenWeather
                val response = repository.getCurrentWeather(lat, lon, com.example.atmoshpere.BuildConfig.API_KEY)
                repository.insertLocation(
                    FavoriteLocation(
                        cityName = name, 
                        latitude = lat, 
                        longitude = lon, 
                        countryName = country,
                        timezoneOffset = response.timezone
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback with timezone 0
                repository.insertLocation(
                    FavoriteLocation(
                        cityName = name, 
                        latitude = lat, 
                        longitude = lon, 
                        countryName = country,
                        timezoneOffset = 0
                    )
                )
            }
        }
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
            if (query.length < 3) {
                _autocompleteResults.value = emptyList()
                return@launch
            }
            try {
                val response = com.example.atmoshpere.data.remote.GeocodingClient.geocodingApi.autocomplete(query)
                if (response.isSuccessful) {
                    _autocompleteResults.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reverseGeocode(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = com.example.atmoshpere.data.remote.GeocodingClient.geocodingApi.reverseGeocode(lat.toString(), lon.toString())
                if (response.isSuccessful) {
                    _reverseGeocodeResult.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearReverseGeocode() { _reverseGeocodeResult.value = null }

    fun removeLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    fun fetchLocationAndWeather() {
        android.util.Log.d("AtmosphereDebug", "fetchLocationAndWeather Called")
        
        val context = getApplication<android.app.Application>()
        val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasCoarse || hasFine) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        android.util.Log.d("AtmosphereDebug", "Location found: ${location.latitude}, ${location.longitude}")
                        fetchWeather(location.latitude, location.longitude)
                    } else {
                        android.util.Log.d("AtmosphereDebug", "lastLocation was null, fetching London")
                        fetchWeather(51.5074, -0.1278) // London Fallback
                    }
                }.addOnFailureListener {
                    android.util.Log.e("AtmosphereDebug", "Failed to get location: ${it.message}")
                    fetchWeather(51.5074, -0.1278) // London Fallback
                }
            } catch (e: SecurityException) {
                android.util.Log.e("AtmosphereDebug", "SecurityException fetching location: ${e.message}")
                fetchWeather(51.5074, -0.1278)
            }
        } else {
            android.util.Log.d("AtmosphereDebug", "Location permission NOT granted, fetching London")
            fetchWeather(51.5074, -0.1278)
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        android.util.Log.d("AtmosphereDebug", "fetchWeather Called for $lat, $lon")
        viewModelScope.launch {
            _isRefreshing.value = true
            
            val currentJob = launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    android.util.Log.d("AtmosphereDebug", "Current Call started")
                    val current = repository.getCurrentWeather(lat, lon, BuildConfig.API_KEY)
                    _currentWeather.value = current
                    android.util.Log.d("AtmosphereDebug", "Current weather saved")
                } catch (e: Exception) {
                    android.util.Log.e("AtmosphereDebug", "Current API failed: ${e.message}", e)
                }
            }

            val forecastJob = launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    android.util.Log.d("AtmosphereDebug", "Forecast Call started")
                    val forecastData = repository.getFiveDayForecast(lat, lon, BuildConfig.API_KEY)
                    _forecast.value = forecastData
                    android.util.Log.d("AtmosphereDebug", "Forecast saved")
                } catch (e: Exception) {
                    android.util.Log.e("AtmosphereDebug", "Forecast API failed: ${e.message}", e)
                }
            }

            currentJob.join()
            forecastJob.join()
            _isRefreshing.value = false
            android.util.Log.d("AtmosphereDebug", "fetchWeather Completed")
        }
    }

    // --- ALERTS CRUD & SCHEDULING ---

    fun addAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.insertAlert(alert)
            if (alert.isActive) scheduleAlarm(alert)
        }
    }

    fun removeAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            cancelAlarm(alert)
        }
    }

    fun toggleAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            val newIsActive = !alert.isActive
            repository.updateAlertStatus(alert.id, newIsActive)
            if (newIsActive) scheduleAlarm(alert.copy(isActive = true)) else cancelAlarm(alert)
        }
    }

    fun updateAlert(oldAlert: WeatherAlert, newAlert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteAlert(oldAlert)
            repository.insertAlert(newAlert)
            cancelAlarm(oldAlert)
            if (newAlert.isActive) scheduleAlarm(newAlert)
        }
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
