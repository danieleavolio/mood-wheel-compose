package com.example.moodwheel

import android.app.Application
import com.example.moodwheel.data.local.AppDatabase
import com.example.moodwheel.data.repository.MoodRepository

class MoodWheelApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        container = AppContainer(
            repository = MoodRepository(database.entryDao())
        )
    }
}

data class AppContainer(
    val repository: MoodRepository
)
