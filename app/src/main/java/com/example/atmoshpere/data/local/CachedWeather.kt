package com.example.atmoshpere.data.local

import androidx.room.*
import com.example.atmoshpere.data.remote.CurrentWeatherResponse
import com.google.gson.Gson

@Entity(tableName = "cached_weather")
data class CachedWeather(
    @PrimaryKey val id: Int = 1,
    val weatherResponse: CurrentWeatherResponse? = null
)

class Converters {
    @TypeConverter
    fun fromWeatherResponse(value: CurrentWeatherResponse?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toWeatherResponse(value: String?): CurrentWeatherResponse? {
        return Gson().fromJson(value, CurrentWeatherResponse::class.java)
    }
}
