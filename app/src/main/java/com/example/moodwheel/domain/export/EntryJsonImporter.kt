package com.example.moodwheel.domain.export

import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class EntryJsonImporter {
    fun import(json: String): List<MoodEntry> {
        val trimmed = json.trim()
        val items = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            JSONObject(trimmed).getJSONArray("entries")
        }

        return buildList {
            repeat(items.length()) { index ->
                val entry = runCatching {
                    val item = items.getJSONObject(index)
                    val emotions = parseEmotions(item)
                    MoodEntry(
                        id = 0,
                        timestamp = parseTimestamp(item),
                        moodLevel = MoodLevel.fromValue(item.optInt("moodLevel", 3)),
                        primaryEmotion = emotions.first(),
                        primaryEmotions = emotions,
                        secondaryEmotions = item.optJSONArray("secondaryEmotions").toStrings(),
                        note = item.optString("note", "").take(300)
                    )
                }.getOrNull()

                if (entry != null) add(entry)
            }
        }
    }

    private fun parseTimestamp(item: JSONObject): Long {
        val raw = item.opt("timestamp")
        return when (raw) {
            is Number -> raw.toLong()
            else -> Instant.parse(item.getString("timestamp")).toEpochMilli()
        }
    }

    private fun parseEmotions(item: JSONObject): List<com.example.moodwheel.domain.model.MacroEmotion> {
        val ids = item.optJSONArray("primaryEmotionIds").toStrings()
        val labels = item.optJSONArray("primaryEmotions").toStrings()
        val parsed = (ids + labels).mapNotNull { raw ->
            EmotionCatalog.emotions.firstOrNull { emotion ->
                emotion.id == raw || emotion.label.equals(raw, ignoreCase = true)
            }
        }.distinctBy { it.id }

        if (parsed.isNotEmpty()) return parsed

        return listOf(
            EmotionCatalog.emotions.firstOrNull { emotion ->
                emotion.id == item.optString("primaryEmotionId") ||
                    emotion.label.equals(item.optString("primaryEmotion"), ignoreCase = true)
            } ?: EmotionCatalog.emotions.first()
        )
    }
}

private fun JSONArray?.toStrings(): List<String> {
    if (this == null) return emptyList()
    return buildList {
        repeat(length()) { index ->
            optString(index).trim().takeIf { it.isNotBlank() }?.let(::add)
        }
    }
}
