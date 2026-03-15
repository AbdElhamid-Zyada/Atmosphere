package com.example.atmoshpere.ui.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsManager {
    val morningBriefEnabled: Flow<Boolean> = flowOf(false)
    val morningBriefHour: Flow<Int> = flowOf(8)
    val morningBriefMinute: Flow<Int> = flowOf(0)
}
