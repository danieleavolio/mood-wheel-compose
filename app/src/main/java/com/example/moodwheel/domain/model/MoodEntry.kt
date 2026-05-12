package com.example.moodwheel.domain.model

data class MoodEntry(
    val id: Long = 0,
    val timestamp: Long,
    val moodLevel: MoodLevel,
    val primaryEmotion: MacroEmotion,
    val primaryEmotions: List<MacroEmotion> = listOf(primaryEmotion),
    val secondaryEmotions: List<String>,
    val note: String
)
