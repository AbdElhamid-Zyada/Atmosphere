package com.example.atmoshpere.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atmoshpere.ui.screens.*
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel

enum class AppDestination(val route: String, val icon: ImageVector, val title: String) {
    HOME("home", Icons.Filled.Home, "Home"),
    FAVORITES("favorites", Icons.Filled.Favorite, "Favorites"),
    ALERTS("alerts", Icons.Filled.Notifications, "Alerts"),
    SETTINGS("settings", Icons.Filled.Settings, "Settings")
}

@Composable
fun AtmosphereApp() {
    var currentRoute by remember { mutableStateOf(AppDestination.HOME.route) }
    var selectedLocation by remember { mutableStateOf<com.example.atmoshpere.data.local.FavoriteLocation?>(null) }
    var showAddLocation by remember { mutableStateOf(false) }
    val weatherViewModel: WeatherViewModel = viewModel()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4DA0FF), Color(0xFF00E5FF))
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (selectedLocation == null && !showAddLocation) {
                    NavigationBar(
                        containerColor = Color(0xFF00E5FF),
                        tonalElevation = 8.dp
                    ) {
                        AppDestination.values().forEach { destination ->
                            NavigationBarItem(
                                icon = { Icon(destination.icon, contentDescription = destination.title) },
                                label = { Text(destination.title) },
                                selected = currentRoute == destination.route,
                                onClick = { currentRoute = destination.route },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                    indicatorColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Modifier.padding(paddingValues).let { defaultModifier ->
                if (selectedLocation != null) {
                    HomeScreen(
                        viewModel = weatherViewModel,
                        location = selectedLocation,
                        onBack = { selectedLocation = null },
                        modifier = defaultModifier
                    )
                } else if (showAddLocation) {
                    AddLocationScreen(
                        viewModel = weatherViewModel,
                        onBack = { showAddLocation = false },
                        modifier = defaultModifier
                    )
                } else {
                    when (currentRoute) {
                        AppDestination.HOME.route -> HomeScreen(viewModel = weatherViewModel, modifier = defaultModifier)
                        AppDestination.FAVORITES.route -> FavoritesScreen(
                            viewModel = weatherViewModel,
                            onLocationClick = { selectedLocation = it },
                            onAddLocationClick = { showAddLocation = true },
                            modifier = defaultModifier
                        )
                        AppDestination.ALERTS.route -> AlertsScreen(viewModel = weatherViewModel, modifier = defaultModifier)
                        AppDestination.SETTINGS.route -> SettingsScreen(viewModel = weatherViewModel, modifier = defaultModifier)
                    }
                }
            }
        }
    }
}
