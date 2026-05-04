package com.example.moodwheel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF5D4AE3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7E1FF),
    onPrimaryContainer = Color(0xFF201065),
    secondary = Color(0xFF6CC35B),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFF3F0FA),
    background = Color(0xFFFFFBFF),
    outline = Color(0xFFE0DDE8)
)

@Composable
fun MoodWheelTheme(
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) LightColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
