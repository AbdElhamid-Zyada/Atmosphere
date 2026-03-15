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
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsPrefs = remember { com.example.atmoshpere.data.local.SettingsPreferences(context) }
    val language by settingsPrefs.language.collectAsState(initial = "en")
    val appearance by settingsPrefs.appearance.collectAsState(initial = "DARK_GLASS")

    val layoutDirection = if (language == "ar") androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr

    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
        var currentRoute by remember { mutableStateOf(AppDestination.HOME.route) }
        var selectedLocation by remember { mutableStateOf<com.example.atmoshpere.data.local.FavoriteLocation?>(null) }
        var showAddLocation by remember { mutableStateOf(false) }
        val weatherViewModel: WeatherViewModel = viewModel()

        val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
        val isDark = when(appearance) {
            "DARK_GLASS" -> true
            "FROST_WHITE" -> false
            else -> isSystemDark
        }

        val backgroundColor = if (isDark) Color(0xFF0B1120) else Color(0xFFE8F1F8)
        val backgroundBottomColor = if (isDark) Color(0xFF131A2D) else Color(0xFFCFDEF3)

        val bgGradient = Brush.verticalGradient(
            colors = listOf(backgroundColor, backgroundBottomColor)
        )

        Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (selectedLocation == null && !showAddLocation) {
                        NavigationBar(
                            containerColor = if (isDark) Color(0xFF131A2D).copy(alpha = 0.95f) else Color(0xFF00E5FF),
                            tonalElevation = 8.dp
                        ) {
                            AppDestination.values().forEach { destination ->
                                val label = com.example.atmoshpere.ui.utils.Translations.get(destination.title, language)
                                NavigationBarItem(
                                    icon = { Icon(destination.icon, contentDescription = label) },
                                    label = { Text(label) },
                                    selected = currentRoute == destination.route,
                                    onClick = { currentRoute = destination.route },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = if (isDark) Color(0xFF00E5FF) else Color.White,
                                        selectedTextColor = if (isDark) Color(0xFF00E5FF) else Color.White,
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
}
