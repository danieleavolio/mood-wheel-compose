package com.example.moodwheel.data.local

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val moodLevel: Int,
    val primaryEmotion: String,
    @ColumnInfo(defaultValue = "'[]'")
    val primaryEmotions: List<String> = emptyList(),
    val secondaryEmotions: List<String>,
    val note: String
)
