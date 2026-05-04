package com.example.moodwheel.data.repository

import com.example.moodwheel.data.local.EntryDao
import com.example.moodwheel.data.local.EntryEntity
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class MoodRepository(
    private val dao: EntryDao,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    fun observeAll(): Flow<List<MoodEntry>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    fun observeLatest(): Flow<MoodEntry?> =
        dao.observeLatest().map { it?.toDomain() }

    fun observeDay(date: LocalDate): Flow<List<MoodEntry>> {
        val start = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return dao.observeBetween(start, end).map { entities -> entities.map { it.toDomain() } }
    }

    fun observeMonth(month: LocalDate): Flow<List<MoodEntry>> {
        val first = month.withDayOfMonth(1)
        val start = first.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = first.plusMonths(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return dao.observeBetween(start, end).map { entities -> entities.map { it.toDomain() } }
    }

    suspend fun add(entry: MoodEntry): Long =
        dao.insert(entry.toEntity())

    suspend fun seedIfEmpty(entries: List<MoodEntry>) {
        if (dao.count() == 0) {
            entries.forEach { dao.insert(it.toEntity()) }
        }
    }

    private fun EntryEntity.toDomain(): MoodEntry =
        MoodEntry(
            id = id,
            timestamp = timestamp,
            moodLevel = MoodLevel.fromValue(moodLevel),
            primaryEmotion = EmotionCatalog.byId(primaryEmotion),
            secondaryEmotions = secondaryEmotions,
            note = note
        )

    private fun MoodEntry.toEntity(): EntryEntity =
        EntryEntity(
            id = id,
            timestamp = timestamp,
            moodLevel = moodLevel.value,
            primaryEmotion = primaryEmotion.id,
            secondaryEmotions = secondaryEmotions,
            note = note
        )
}
