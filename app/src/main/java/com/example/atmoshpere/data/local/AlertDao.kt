package com.example.atmoshpere.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: WeatherAlert): Long

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)

    @Query("UPDATE weather_alerts SET isActive = :isActive WHERE id = :id")
    suspend fun updateAlertStatus(id: String, isActive: Boolean)
}
