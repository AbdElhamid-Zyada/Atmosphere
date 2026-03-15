package com.example.atmoshpere.ui.screens

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
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
    
    var selectedGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedLocationNode by remember { mutableStateOf<GeocodingResponse?>(null) }
    val reverseResult by viewModel.reverseGeocodeResult.collectAsState()

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

    // Monitor tap or search result selection triggers
    LaunchedEffect(selectedGeoPoint) {
        if (selectedGeoPoint != null) {
            viewModel.reverseGeocode(selectedGeoPoint!!.latitude, selectedGeoPoint!!.longitude)
        }
    }

    LaunchedEffect(reverseResult) {
        if (reverseResult != null) {
            selectedLocationNode = reverseResult
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

                    // Add Taps Events Overlay
                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            selectedGeoPoint = p
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                    val overlay = MapEventsOverlay(mapEventsReceiver)
                    overlays.add(overlay)
                }
            },
            update = { view ->
                if (selectedGeoPoint != null) {
                    view.overlays.removeAll { it is Marker }
                    val marker = Marker(view).apply {
                        position = selectedGeoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = selectedLocationNode?.displayName ?: "Selected Point"
                    }
                    view.overlays.add(marker)
                    view.controller.animateTo(selectedGeoPoint)
                    // only zoom to 12.0 the FIRST time the tapped point sets
                    if (view.zoomLevelDouble < 10.0) {
                        view.controller.setZoom(12.0)
                    }
                    view.invalidate()
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
                IconButton(onClick = {
                    viewModel.clearReverseGeocode()
                    onBack()
                }) {
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
                                        selectedLocationNode = item
                                        selectedGeoPoint = GeoPoint(item.lat.toDouble(), item.lon.toDouble())
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

        // 3. Transparent Center Marker Label (to match absolute screenshots if necessary)
        // But Marker overlay handles it perfectly with a tooltip.

        // 4. Confirm Bottom Card
        if (selectedLocationNode != null) {
            val loc = selectedLocationNode!!
            val city = loc.address?.city ?: loc.address?.town ?: loc.address?.village ?: loc.displayName.split(",")[0]
            val country = loc.address?.country ?: "Location"
            val state = loc.address?.town ?: "" // can use town or state fields

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = city.trim(),
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (state.isNotEmpty() && state != city) "$state, $country" else country,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.addLocation(
                                    name = city.trim(),
                                    lat = selectedGeoPoint?.latitude ?: loc.lat.toDouble(),
                                    lon = selectedGeoPoint?.longitude ?: loc.lon.toDouble(),
                                    country = country.trim()
                                )
                                viewModel.clearReverseGeocode()
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select This Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
