package com.example.moodwheel.data.repository

import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import java.time.LocalDateTime
import java.time.ZoneId

object SampleData {
    fun entries(zoneId: ZoneId = ZoneId.systemDefault()): List<MoodEntry> {
        fun at(daysAgo: Long, hour: Int, minute: Int): Long =
            LocalDateTime.now()
                .minusDays(daysAgo)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()

        return listOf(
            MoodEntry(
                timestamp = at(0, 18, 45),
                moodLevel = MoodLevel.Good,
                primaryEmotion = EmotionCatalog.byId("happiness"),
                secondaryEmotions = listOf("sereno", "grato", "fiducioso"),
                note = "Giornata tranquilla. Ho passato del tempo con la mia famiglia."
            ),
            MoodEntry(
                timestamp = at(0, 13, 10),
                moodLevel = MoodLevel.Bad,
                primaryEmotion = EmotionCatalog.byId("sadness"),
                secondaryEmotions = listOf("stanco", "solo"),
                note = "Giornata pesante al lavoro."
            ),
            MoodEntry(
                timestamp = at(1, 20, 15),
                moodLevel = MoodLevel.Bad,
                primaryEmotion = EmotionCatalog.byId("disgust"),
                secondaryEmotions = listOf("deluso"),
                note = "Notizia che non mi ha fatto piacere."
            ),
            MoodEntry(
                timestamp = at(2, 16, 40),
                moodLevel = MoodLevel.VeryGood,
                primaryEmotion = EmotionCatalog.byId("surprise"),
                secondaryEmotions = listOf("curioso", "meravigliato"),
                note = "Regalo inaspettato."
            )
        )
    }
}
