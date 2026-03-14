package com.example.atmoshpere.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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

class AlarmActivity : ComponentActivity() {
    private var ringtone: Ringtone? = null

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
        
        // Play Alarm Sound
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: 
                      RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        ringtone?.play()

        setContent {
            AtmosphereTheme {
                val viewModel: WeatherViewModel = viewModel()
                
                // Fetch latest weather for current location
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    viewModel.fetchLocationAndWeather()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black // Dark background for alarm
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 64.dp, bottom = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "WEATHER ALARM",
                                    color = Color.Red,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Box(modifier = Modifier.weight(1f)) {
                                HomeScreenContent(viewModel = viewModel)
                            }

                            Button(
                                onClick = { 
                                    ringtone?.stop()
                                    finish() 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                                    .height(64.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(32.dp)
                            ) {
                                Text("DISMISS", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}
