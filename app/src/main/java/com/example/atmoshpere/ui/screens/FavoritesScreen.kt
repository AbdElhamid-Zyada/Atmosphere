package com.example.atmoshpere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmoshpere.data.local.FavoriteLocation
import com.example.atmoshpere.ui.components.AtmosphereConfirmDialog
import com.example.atmoshpere.ui.components.GlassCard
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: WeatherViewModel, 
    onLocationClick: (com.example.atmoshpere.data.local.FavoriteLocation) -> Unit,
    onAddLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsPrefs = remember { com.example.atmoshpere.data.local.SettingsPreferences(context) }
    val language by settingsPrefs.language.collectAsState(initial = "en")

    val favorites by viewModel.favorites.collectAsState()
    var locationToDelete by remember { mutableStateOf<com.example.atmoshpere.data.local.FavoriteLocation?>(null) }
    
    // Minute Clock Timer Tick
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60000)
            currentTime = System.currentTimeMillis()
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Favorites",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).padding(bottom = 80.dp)
            ) {
                items(favorites, key = { it.id }) { location ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                locationToDelete = location
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.Red.copy(alpha = 0.6f))
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                // Empty background while swiping
                            }
                        },
                        enableDismissFromStartToEnd = false
                    ) {
                        FavoriteCard(
                            location = location,
                            currentTime = currentTime,
                            onClick = { onLocationClick(location) }
                        )
                    }
                }
            }
        }

        // Add Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Button(
                onClick = { onAddLocationClick() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(com.example.atmoshpere.ui.utils.Translations.get("+ ADD LOCATION", language), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        locationToDelete?.let { location ->
            AtmosphereConfirmDialog(
                title = com.example.atmoshpere.ui.utils.Translations.get("Remove Location", language),
                message = "${com.example.atmoshpere.ui.utils.Translations.get("Are you sure you want to remove", language)} ${location.cityName}?",
                onConfirm = {
                    viewModel.removeLocation(location)
                    locationToDelete = null
                },
                onDismiss = { locationToDelete = null }
            )
        }
    }
}

@Composable
fun FavoriteCard(
    location: com.example.atmoshpere.data.local.FavoriteLocation,
    currentTime: Long,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsPrefs = remember { com.example.atmoshpere.data.local.SettingsPreferences(context) }
    val appearance by settingsPrefs.appearance.collectAsState(initial = "DARK_GLASS")

    val isDark = when (appearance) {
        "DARK_GLASS" -> true
        "FROST_WHITE" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val cardColor = if (isDark) Color(0xFF1F2937) else Color.White

    // Calculate Local Time based on timezoneOffset (seconds)
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = currentTime
    calendar.add(Calendar.SECOND, location.timezoneOffset)
    
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val formattedTime = sdfTime.format(calendar.time)
    
    val hours = location.timezoneOffset / 3600
    val timeZoneLabel = if (hours >= 0) "GMT+$hours" else "GMT$hours" // Can fallback to specific labels if known

    GlassCard(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).clickable { onClick() }, 
        alpha = if (isDark) 0.4f else 0.15f,
        containerColor = cardColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), 
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    location.countryName.ifEmpty { "Location" }, 
                    color = Color.White, 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    location.cityName, 
                    color = Color.White.copy(alpha = 0.8f), 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formattedTime, 
                    color = Color.White, 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    timeZoneLabel, 
                    color = Color.White.copy(alpha = 0.6f), 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
