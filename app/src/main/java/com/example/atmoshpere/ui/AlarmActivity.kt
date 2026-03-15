package com.example.atmoshpere.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atmoshpere.ui.screens.HomeScreenContent
import com.example.atmoshpere.ui.theme.AtmosphereTheme
import com.example.atmoshpere.ui.viewmodel.WeatherViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import android.media.RingtoneManager
import android.media.Ringtone
import kotlin.math.round

class AlarmActivity : ComponentActivity() {
    private var mediaPlayer: android.media.MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure the screen turns on and shows over lockscreen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        
        enableEdgeToEdge()
        
        try {
            val alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer.create(this, alertSound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            AtmosphereTheme {
                val viewModel: WeatherViewModel = viewModel()
                
                // Fetch latest weather for current location
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    viewModel.fetchLocationAndWeather()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0B1220)
                ) {
                    val currentWeather = viewModel.currentWeather.collectAsState().value

                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(64.dp))
                        Text(
                            "🚨 LOOK OUTSIDE! 🚨",
                            color = Color(0xFF00E5FF),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Today's weather brief is here",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (currentWeather != null) {
                            val weather = currentWeather!!
                            Text(
                                com.example.atmoshpere.ui.screens.getWeatherEmoji(weather.weather.firstOrNull()?.main ?: "Clear"),
                                fontSize = 100.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "${weather.main.temp.toInt()}°",
                                color = Color.White,
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Feels like ${weather.main.feelsLike.toInt()}° • ${weather.name}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        } else {
                            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF00E5FF))
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { 
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                finish() 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("DISMISS", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }
}
