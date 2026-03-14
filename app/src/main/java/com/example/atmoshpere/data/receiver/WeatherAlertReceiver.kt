package com.example.atmoshpere.data.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.atmoshpere.R
import com.example.atmoshpere.ui.AlarmActivity

class WeatherAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getStringExtra("ALERT_ID") ?: return
        val alertType = intent.getStringExtra("ALERT_TYPE") ?: "NOTIFICATION"

        if (alertType == "ALERT") {
            // Start Alarm Screen Direct
            val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("ALERT_ID", alertId)
            }
            context.startActivity(alarmIntent)
        } else {
            // Show Notification instead
            showNotification(context, alertId)
        }
    }

    private fun showNotification(context: Context, alertId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_alerts_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Action when pressing notification -> Open AlarmActivity or MainActivity
        val openIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("ALERT_ID", alertId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon
            .setContentTitle("Weather Alert Triggered!")
            .setContentText("Tap to view current status.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alertId.hashCode(), notification)
    }
}
