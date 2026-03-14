package com.example.atmoshpere.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.atmoshpere.data.local.WeatherAlert
import java.util.*

@Composable
fun AtmosphereConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = { Text(message, color = Color.White.copy(alpha = 0.8f)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("YES", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("NO", color = Color.White.copy(alpha = 0.6f))
            }
        },
        containerColor = Color(0xFF1A1A1A), // Dark mode dialog shape
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun AddAlertDialog(
    alert: WeatherAlert? = null,
    onDismiss: () -> Unit,
    onSave: (WeatherAlert) -> Unit
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(alert?.type ?: "ALERT") }
    var startTime by remember { mutableStateOf(alert?.startTime ?: "08:00") }
    var endTime by remember { mutableStateOf(alert?.endTime ?: "14:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (alert == null) "Add Weather Alert" else "Edit Weather Alert",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Type Switcher
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f).clickable { selectedType = "ALERT" }
                            .background(if (selectedType == "ALERT") Color(0xFF00E5FF) else Color.Transparent)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ALERT", color = if (selectedType == "ALERT") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier.weight(1f).clickable { selectedType = "NOTIFICATION" }
                            .background(if (selectedType == "NOTIFICATION") Color(0xFF00E5FF) else Color.Transparent)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("NOTIFICATION", color = if (selectedType == "NOTIFICATION") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Time Pickers
                TimeSelectorItem("Starts At", startTime) { hour, minute ->
                    startTime = String.format("%02d:%02d", hour, minute)
                }
                TimeSelectorItem("Ends At", endTime) { hour, minute ->
                    endTime = String.format("%02d:%02d", hour, minute)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newAlert = alert?.copy(type = selectedType, startTime = startTime, endTime = endTime)
                    ?: WeatherAlert(type = selectedType, startTime = startTime, endTime = endTime)
                onSave(newAlert)
            }) {
                Text(if (alert == null) "ADD" else "SAVE", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.White.copy(alpha = 0.6f))
            }
        },
        containerColor = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun TimeSelectorItem(label: String, time: String, onTimeSelected: (Int, Int) -> Unit) {
    val context = LocalContext.current
    val parts = time.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()

    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            TimePickerDialog(context, { _, h, m -> onTimeSelected(h, m) }, hour, minute, true).show()
        }.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White)
        Text(time, color = Color(0xFF00E5FF), fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
