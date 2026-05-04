package com.example.moodwheel.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries ORDER BY timestamp DESC LIMIT 1")
    fun observeLatest(): Flow<EntryEntity?>

    @Query("SELECT * FROM entries WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<EntryEntity>>

    @Query("SELECT COUNT(*) FROM entries")
    suspend fun count(): Int

    @Insert
    suspend fun insert(entry: EntryEntity): Long

    @Update
    suspend fun update(entry: EntryEntity)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun delete(id: Long)
}
