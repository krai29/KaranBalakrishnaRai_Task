package com.krai29.karanbalakrishnaraitask.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0A3D91),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onSurface = Color(0xFF222222)
)

@Composable
fun KaranBalakrishnaRaiTaskTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}