package com.example.atmoshpere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmoshpere.ui.components.GlassCard
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel
import com.example.atmoshpere.ui.screens.getWeatherEmoji
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        viewModel.fetchLocationAndWeather()
    }

    HomeScreenContent(viewModel = viewModel, modifier = modifier)
}

@Composable
fun HomeScreenContent(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val currentWeather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    if (currentWeather == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isRefreshing) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("No weather data available", color = Color.White)
            }
        }
        return
    }

    val current = currentWeather!!
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdfDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = sdfTime.format(Date()),
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(current.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text(sdfDate.format(Date()), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(getWeatherEmoji(current.weather.firstOrNull()?.icon), fontSize = 64.sp)
        }

        Text("${current.main.temp.roundToInt()}°C", color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Bold)
        Text(current.weather.firstOrNull()?.main ?: "Clear", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(32.dp))

        // Re-added streamlined approach
        GlassCard(modifier = Modifier.fillMaxWidth(), alpha = 0.15f) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                InfoCard(value = "${current.main.humidity}%", icon = "💧")
                InfoCard(value = "${current.wind.speed.roundToInt()}km/h", icon = "💨")
                InfoCard(value = "${current.main.pressure}hPa", icon = "🌡")
                InfoCard(value = "${current.clouds.all}%", icon = "☁️")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (forecast != null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("Hourly Forecast", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val hourlies = forecast!!.list.take(8)
                items(hourlies.size) { i ->
                    val hourly = hourlies[i]
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(hourly.dt * 1000))
                    GlassCard(
                        modifier = Modifier.width(70.dp).height(110.dp),
                        alpha = 0.15f
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(time, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(getWeatherEmoji(hourly.weather.firstOrNull()?.icon), fontSize = 20.sp)
                            Text("${hourly.main.temp.roundToInt()}°", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("5-Day Forecast", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                val groupedByDay = forecast!!.list.groupBy {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.dt * 1000))
                }.values.take(5)

                groupedByDay.forEach { dailyList ->
                    val first = dailyList.first()
                    val max = dailyList.maxOf { it.main.temp_max }
                    val min = dailyList.minOf { it.main.temp_min }
                    val dayStr = SimpleDateFormat("EEE\nndd MMM", Locale.getDefault()).format(Date(first.dt * 1000)).replace("\nn", "\n")

                    GlassCard(modifier = Modifier.fillMaxWidth(), alpha = 0.15f) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            val dp = dayStr.split("\n")
                            Column {
                                Text(dp[0], color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (dp.size > 1) Text(dp[1], color = Color.White.copy(alpha=0.7f), fontSize = 12.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(getWeatherEmoji(first.weather.firstOrNull()?.icon), fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(first.weather.firstOrNull()?.main ?: "", color = Color.White, fontSize = 14.sp)
                            }

                            Row {
                                Text("${max.roundToInt()}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${min.roundToInt()}°", color = Color.White.copy(alpha=0.6f), fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

fun getWeatherEmoji(iconCode: String?): String {
    return when (iconCode) {
        "01d" -> "☀️"
        "01n" -> "🌙"
        "02d" -> "⛅"
        "02n" -> "☁️"
        "03d", "03n", "04d", "04n" -> "☁️"
        "09d", "09n" -> "🌧"
        "10d", "10n" -> "🌦"
        "11d", "11n" -> "⛈"
        "13d", "13n" -> "❄️"
        "50d", "50n" -> "🌫"
        else -> "☀️"
    }
}
