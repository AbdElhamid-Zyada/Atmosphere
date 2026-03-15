package com.example.atmoshpere.data.remote

import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String): com.example.atmoshpere.data.remote.CurrentWeatherResponse
    suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String): com.example.atmoshpere.data.remote.FiveDayForecastResponse

    suspend fun geocode(cityName: String, limit: Int): List<com.example.atmoshpere.data.remote.GeocodingResponse>
    suspend fun reverseGeocode(lat: Double, lon: Double): com.example.atmoshpere.data.remote.GeocodingResponse
}

class RemoteDataSourceImpl(
    private val weatherApi: com.example.atmoshpere.data.remote.WeatherApi,
    private val geocodingApi: com.example.atmoshpere.data.remote.GeocodingApi
) : IRemoteDataSource {
    override suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String, units: String) = weatherApi.getCurrentWeather(lat, lon, apiKey, units)
    override suspend fun getFiveDayForecast(lat: Double, lon: Double, apiKey: String, units: String) = weatherApi.getFiveDayForecast(lat, lon, apiKey, units)

    override suspend fun geocode(cityName: String, limit: Int): List<com.example.atmoshpere.data.remote.GeocodingResponse> {
        return geocodingApi.autocomplete(query = cityName, limit = limit).body() ?: emptyList()
    }

    override suspend fun reverseGeocode(lat: Double, lon: Double): com.example.atmoshpere.data.remote.GeocodingResponse {
        return geocodingApi.reverseGeocode(lat = lat.toString(), lon = lon.toString()).body()!!
    }
}
