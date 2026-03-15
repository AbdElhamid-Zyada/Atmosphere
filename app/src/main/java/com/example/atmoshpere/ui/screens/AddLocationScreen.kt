package com.example.atmoshpere.ui.screens

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.atmoshpere.data.remote.GeocodingResponse
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    viewModel: WeatherViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val suggestions by viewModel.autocompleteResults.collectAsState()
    
    var selectedLocation by remember { mutableStateOf<GeocodingResponse?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // OSM Configuration Initialization
    LaunchedEffect(Unit) {
        org.osmdroid.config.Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        )
    }

    // Throttle Search debouce
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            kotlinx.coroutines.delay(500)
            viewModel.searchLocation(searchQuery)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Embedded OSM Map
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(20.0, 0.0)) // Default Center
                    mapViewRef = this
                }
            },
            update = { view ->
                selectedLocation?.let { loc ->
                    view.overlays.clear()
                    val geoPoint = GeoPoint(loc.lat.toDouble(), loc.lon.toDouble())
                    val marker = Marker(view).apply {
                        position = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = loc.displayName
                    }
                    view.overlays.add(marker)
                    view.controller.animateTo(geoPoint)
                    view.controller.setZoom(12.0)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Search Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Text("←", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search city or country...") },
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            if (suggestions.isNotEmpty() && searchQuery.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    tonalElevation = 4.dp,
                    color = Color.White
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(suggestions) { item ->
                            Text(
                                text = item.displayName,
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = item
                                        searchQuery = "" // Clear suggestions
                                    }
                                    .padding(16.dp),
                                fontSize = 14.sp
                            )
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }

        // 3. Confirm Bottom Button
        if (selectedLocation != null) {
            Button(
                onClick = {
                    val loc = selectedLocation!!
                    val city = loc.address?.city ?: loc.address?.town ?: loc.displayName.split(",")[0]
                    val country = loc.address?.country ?: "Unknown"
                    viewModel.addLocation(
                        name = city.trim(),
                        lat = loc.lat.toDouble(),
                        lon = loc.lon.toDouble(),
                        country = country.trim()
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Select This Location", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
