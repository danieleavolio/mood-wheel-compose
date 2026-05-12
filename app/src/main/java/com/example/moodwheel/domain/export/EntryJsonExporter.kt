package com.example.moodwheel.domain.export

import com.example.moodwheel.domain.model.MoodEntry
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class EntryJsonExporter {
    fun export(entries: List<MoodEntry>): String {
        val items = JSONArray()
        entries.sortedByDescending { it.timestamp }.forEach { entry ->
            items.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("timestamp", Instant.ofEpochMilli(entry.timestamp).toString())
                    .put("moodLevel", entry.moodLevel.value)
                    .put("primaryEmotion", entry.primaryEmotion.label)
                    .put("primaryEmotionId", entry.primaryEmotion.id)
                    .put("primaryEmotions", JSONArray(entry.primaryEmotions.map { it.label }))
                    .put("primaryEmotionIds", JSONArray(entry.primaryEmotions.map { it.id }))
                    .put("secondaryEmotions", JSONArray(entry.secondaryEmotions))
                    .put("note", entry.note)
            )
        }

        return JSONObject()
            .put("schema", "mood-wheel-v1")
            .put("exportedAt", Instant.now().toString())
            .put("entryCount", entries.size)
            .put("entries", items)
            .toString(2)
    }
}
