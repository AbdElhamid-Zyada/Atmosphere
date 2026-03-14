package com.example.atmoshpere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmoshpere.data.local.WeatherAlert
import com.example.atmoshpere.ui.components.AddAlertDialog
import com.example.atmoshpere.ui.components.AtmosphereConfirmDialog
import com.example.atmoshpere.ui.components.GlassCard
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel

@Composable
fun AlertsScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val alerts by viewModel.alerts.collectAsState()
    var alertToEdit by remember { mutableStateOf<WeatherAlert?>(null) }
    var alertToDelete by remember { mutableStateOf<WeatherAlert?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "ALERTS",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).padding(bottom = 80.dp)
            ) {
                items(alerts) { alert ->
                    AlertCard(
                        alert = alert,
                        onToggle = { viewModel.toggleAlert(alert) },
                        onEdit = { alertToEdit = alert },
                        onDelete = { alertToDelete = alert }
                    )
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
                Text("+ ADD ALERT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddAlertDialog(
                onDismiss = { showAddDialog = false },
                onSave = {
                    viewModel.addAlert(it)
                    showAddDialog = false
                }
            )
        }

        alertToEdit?.let { alert ->
            AddAlertDialog(
                alert = alert,
                onDismiss = { alertToEdit = null },
                onSave = {
                    viewModel.updateAlert(alert, it)
                    alertToEdit = null
                }
            )
        }

        alertToDelete?.let { alert ->
            AtmosphereConfirmDialog(
                title = "Remove Alert",
                message = "Are you sure you want to remove this alert?",
                onConfirm = {
                    viewModel.removeAlert(alert)
                    alertToDelete = null
                },
                onDismiss = { alertToDelete = null }
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AlertCard(
    alert: WeatherAlert,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { },
                onLongClick = onEdit
            ),
        alpha = 0.15f
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(alert.type, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Edit", tint = Color.White)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha=0.6f))
                    }
                }

                Switch(
                    checked = alert.isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("STARTS", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(alert.startTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text("→", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp)
                Column(horizontalAlignment = Alignment.End) {
                    Text("ENDS", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(alert.endTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
