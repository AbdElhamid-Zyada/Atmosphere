package com.example.atmoshpere.data.local

import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM cached_weather WHERE id = :id LIMIT 1")
    suspend fun getCachedWeather(id: Int = 1): CachedWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(weather: CachedWeather)
}
