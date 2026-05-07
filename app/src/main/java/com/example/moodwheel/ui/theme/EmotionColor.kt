package com.example.moodwheel.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.moodwheel.domain.model.MacroEmotion

fun MacroEmotion.color(): Color =
    when (id) {
        "happiness" -> MoodColors.EmotionHappiness
        "sadness" -> MoodColors.EmotionSadness
        "anger" -> MoodColors.EmotionAnger
        "fear" -> MoodColors.EmotionFear
        "disgust" -> MoodColors.EmotionDisgust
        "surprise" -> MoodColors.EmotionSurprise
        else -> Color(colorHex)
    }

fun MacroEmotion.softColor(): Color = color().copy(alpha = 0.24f)
