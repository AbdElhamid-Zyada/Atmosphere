package com.example.atmoshpere.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences(private val context: Context) {

    companion object {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LANGUAGE = stringPreferencesKey("language")
        val USE_CURRENT_LOCATION = booleanPreferencesKey("use_current_location")
        val CUSTOM_LAT = doublePreferencesKey("custom_lat")
        val CUSTOM_LON = doublePreferencesKey("custom_lon")
        val CUSTOM_LOCATION_NAME = stringPreferencesKey("custom_location_name")
        val APPEARANCE = stringPreferencesKey("appearance")
    }

    val tempUnit: Flow<String> = context.dataStore.data.map { it[TEMP_UNIT] ?: "metric" }
    val windUnit: Flow<String> = context.dataStore.data.map { it[WIND_UNIT] ?: "m/s" }
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "en" }
    val useCurrentLocation: Flow<Boolean> = context.dataStore.data.map { it[USE_CURRENT_LOCATION] ?: true }
    val customLat: Flow<Double?> = context.dataStore.data.map { it[CUSTOM_LAT] }
    val customLon: Flow<Double?> = context.dataStore.data.map { it[CUSTOM_LON] }
    val customLocationName: Flow<String> = context.dataStore.data.map { it[CUSTOM_LOCATION_NAME] ?: "Alexandria, Egypt" }
    val appearance: Flow<String> = context.dataStore.data.map { it[APPEARANCE] ?: "DARK_GLASS" }

    suspend fun saveTempUnit(unit: String) {
        context.dataStore.edit { it[TEMP_UNIT] = unit }
    }

    suspend fun saveAppearance(theme: String) {
        context.dataStore.edit { it[APPEARANCE] = theme }
    }

    suspend fun saveWindUnit(unit: String) {
        context.dataStore.edit { it[WIND_UNIT] = unit }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }

    suspend fun saveUseCurrentLocation(use: Boolean) {
        context.dataStore.edit { it[USE_CURRENT_LOCATION] = use }
    }

    suspend fun saveCustomLocation(lat: Double, lon: Double, name: String) {
        context.dataStore.edit {
            it[CUSTOM_LAT] = lat
            it[CUSTOM_LON] = lon
            it[CUSTOM_LOCATION_NAME] = name
        }
    }
}
