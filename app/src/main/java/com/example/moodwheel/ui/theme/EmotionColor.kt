package com.example.moodwheel.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.moodwheel.domain.model.MacroEmotion

fun MacroEmotion.color(): Color = Color(colorHex)

fun MacroEmotion.softColor(): Color = color().copy(alpha = 0.18f)
