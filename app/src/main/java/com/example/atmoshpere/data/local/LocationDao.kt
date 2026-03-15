package com.example.atmoshpere.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorite_locations")
    fun getAllLocations(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavoriteLocation)

    @Delete
    suspend fun deleteLocation(location: FavoriteLocation)

    // Alerts
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: WeatherAlert): Long

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)

    @Query("UPDATE weather_alerts SET isActive = :isActive WHERE id = :id")
    suspend fun updateAlertStatus(id: String, isActive: Boolean)
}
