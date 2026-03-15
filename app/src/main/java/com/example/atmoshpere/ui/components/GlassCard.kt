package com.example.atmoshpere.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.15f,
    cornerRadius: Int = 24,
    containerColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor.copy(alpha = alpha))
            .border(1.dp, containerColor.copy(alpha = 0.3f), shape)
            .padding(16.dp)
    ) {
        content()
    }
}
