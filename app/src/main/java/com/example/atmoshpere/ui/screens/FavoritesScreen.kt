package com.example.atmoshpere.ui.screens

import androidx.compose.foundation.background
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
fun FavoritesScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val favorites by viewModel.favorites.collectAsState()
    var locationToDelete by remember { mutableStateOf<FavoriteLocation?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

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
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                            }
                        },
                        enableDismissFromStartToEnd = false
                    ) {
                        FavoriteCard(country = "Country", city = location.cityName, timeZone = "12:00\nGMT")
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
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("+ ADD LOCATION", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        if (showAddDialog) {
            // Add dialog implementation if needed on previous screen searches
        }

        locationToDelete?.let { location ->
            AtmosphereConfirmDialog(
                title = "Remove Location",
                message = "Are you sure you want to remove ${location.cityName}?",
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
fun FavoriteCard(country: String, city: String, timeZone: String) {
    GlassCard(modifier = Modifier.fillMaxWidth(), alpha = 0.15f) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(country, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(city, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            val timeParts = timeZone.split("\n")
            Column(horizontalAlignment = Alignment.End) {
                Text(timeParts[0], color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                if (timeParts.size > 1) {
                    Text(timeParts[1], color = Color.White.copy(alpha=0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
