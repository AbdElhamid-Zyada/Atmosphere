package com.example.atmoshpere.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.data.repository.IWeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: IWeatherRepository
) : ViewModel() {

    val favoritesList: StateFlow<List<FavoriteLocation>> = repository.getAllLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.insertFavoriteLocation(location)
        }
    }

    fun deleteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteFavoriteLocation(location)
        }
    }
}
