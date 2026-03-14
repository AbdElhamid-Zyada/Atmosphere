package com.example.atmoshpere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.atmoshpere.data.local.SettingsPreferences
import com.example.atmoshpere.ui.components.GlassCard
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val settingsPrefs = remember { SettingsPreferences(context) }
    val scope = rememberCoroutineScope()

    val tempUnit by settingsPrefs.tempUnit.collectAsState(initial = "metric")
    val windUnit by settingsPrefs.windUnit.collectAsState(initial = "m/s")
    val language by settingsPrefs.language.collectAsState(initial = "en")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Settings",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "UNITS & LOCALIZATION",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), alpha = 0.15f) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Temperature Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Temperature Unit", color = Color.White)
                    val options = listOf("metric", "imperial")
                    val displayOptions = listOf("°C", "°F")
                    val selectedIndex = options.indexOf(tempUnit)
                    SegmentedControl(options = displayOptions, selectedIndex = if(selectedIndex>=0) selectedIndex else 0) {
                        scope.launch { settingsPrefs.saveTempUnit(options[it]) }
                    }
                }

                // Wind Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Wind Speed", color = Color.White)
                    val options = listOf("m/s", "mph")
                    val selectedIndex = options.indexOf(windUnit)
                    SegmentedControl(options = options, selectedIndex = if(selectedIndex>=0) selectedIndex else 0) {
                        scope.launch { settingsPrefs.saveWindUnit(options[it]) }
                    }
                }

                // Language Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Language", color = Color.White)
                    val options = listOf("en", "ar")
                    val displayOptions = listOf("EN", "AR")
                    val selectedIndex = options.indexOf(language)
                    SegmentedControl(options = displayOptions, selectedIndex = if(selectedIndex>=0) selectedIndex else 0) {
                        scope.launch { settingsPrefs.saveLanguage(options[it]) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SegmentedControl(options: List<String>, selectedIndex: Int, onOptionSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
    ) {
        options.forEachIndexed { index, option ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (index == selectedIndex) Color(0xFF00E5FF) else Color.Transparent)
                    .clickable { onOptionSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(option, color = if (index == selectedIndex) Color.Black else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
