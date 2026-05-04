package com.example.moodwheel.data.local

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {
    @TypeConverter
    fun fromStringList(values: List<String>): String = JSONArray(values).toString()

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val array = JSONArray(value)
        return List(array.length()) { index -> array.getString(index) }
    }
}
