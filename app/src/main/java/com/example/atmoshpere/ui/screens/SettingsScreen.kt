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
import com.example.atmoshpere.ui.utils.Translations

@Composable
fun SettingsScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val settingsPrefs = remember { SettingsPreferences(context) }
    val scope = rememberCoroutineScope()

    val tempUnit by settingsPrefs.tempUnit.collectAsState(initial = "metric")
    val windUnit by settingsPrefs.windUnit.collectAsState(initial = "m/s")
    val language by settingsPrefs.language.collectAsState(initial = "en")
    val useCurrentLocation by settingsPrefs.useCurrentLocation.collectAsState(initial = true)
    val customLocationName by settingsPrefs.customLocationName.collectAsState(initial = "Alexandria, Egypt")
    val appearance by settingsPrefs.appearance.collectAsState(initial = "DARK_GLASS")
    
    val isDark = when (appearance) {
        "DARK_GLASS" -> true
        "FROST_WHITE" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val cardColor = if (isDark) Color(0xFF1F2937) else Color.White

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = Translations.get("Settings", language),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = Translations.get("UNITS & LOCALIZATION", language),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), alpha = if (isDark) 0.4f else 0.15f, containerColor = cardColor) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(Translations.get("Temperature Unit", language), color = Color.White)
                    val options = listOf("metric", "imperial", "standard")
                    val displayOptions = listOf("°C", "°F", "K")
                    val selectedIndex = options.indexOf(tempUnit)
                    SegmentedControl(options = displayOptions, selectedIndex = if(selectedIndex>=0) selectedIndex else 0) {
                        scope.launch { settingsPrefs.saveTempUnit(options[it]) }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(Translations.get("Wind Speed", language), color = Color.White)
                    val options = listOf("m/s", "mph")
                    val selectedIndex = options.indexOf(windUnit)
                    SegmentedControl(options = options, selectedIndex = if(selectedIndex>=0) selectedIndex else 0) {
                        scope.launch { settingsPrefs.saveWindUnit(options[it]) }
                    }
                }

                Column {
                    Text(Translations.get("Location", language), color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = useCurrentLocation,
                            onClick = { scope.launch { settingsPrefs.saveUseCurrentLocation(true) } },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF), unselectedColor = Color.White)
                        )
                        Text(Translations.get("Use current location", language), color = Color.White, modifier = Modifier.clickable { scope.launch { settingsPrefs.saveUseCurrentLocation(true) } })
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !useCurrentLocation,
                            onClick = { scope.launch { settingsPrefs.saveUseCurrentLocation(false) } },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF), unselectedColor = Color.White)
                        )
                        Text(Translations.get("Choose custom location", language), color = Color.White, modifier = Modifier.clickable { scope.launch { settingsPrefs.saveUseCurrentLocation(false) } })
                    }

                    if (!useCurrentLocation) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(customLocationName, color = Color.White)
                                Text("CHANGE", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { /* Trigger Search/Dialog */ })
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(Translations.get("Language", language), color = Color.White)
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

        Text(
            text = Translations.get("APPEARANCE", language),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppearanceCard("DARK_GLASS", Translations.get("DARK GLASS", language), Color.Black, appearance == "DARK_GLASS") {
                scope.launch { settingsPrefs.saveAppearance("DARK_GLASS") }
            }
            AppearanceCard("FROST_WHITE", Translations.get("FROST WHITE", language), Color.White.copy(alpha = 0.8f), appearance == "FROST_WHITE") {
                scope.launch { settingsPrefs.saveAppearance("FROST_WHITE") }
            }
            AppearanceCard("DEVICE", Translations.get("DEVICE", language), Color.Transparent, appearance == "DEVICE") {
                scope.launch { settingsPrefs.saveAppearance("DEVICE") }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
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

@Composable
fun AppearanceCard(title: String, displayTitle: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Box(
            modifier = androidx.compose.ui.Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (title == "DEVICE") Color.Transparent else color)
        ) {
            if (title == "DEVICE") {
                Row(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    Box(modifier = androidx.compose.ui.Modifier.weight(1f).fillMaxHeight().background(Color.White))
                    Box(modifier = androidx.compose.ui.Modifier.weight(1f).fillMaxHeight().background(Color(0xFF1E293B)))
                }
            }
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Text(displayTitle, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
