package com.example.moodwheel.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.data.repository.SampleData
import com.example.moodwheel.domain.model.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val latest: MoodEntry? = null,
    val todayEntries: List<MoodEntry> = emptyList(),
    val allEntries: List<MoodEntry> = emptyList()
)

class HomeViewModel(
    private val repository: MoodRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        combine(
            repository.observeLatest(),
            repository.observeDay(LocalDate.now()),
            repository.observeAll()
        ) { latest, today, all ->
            HomeUiState(latest = latest, todayEntries = today, allEntries = all)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    init {
        viewModelScope.launch {
            repository.seedIfEmpty(SampleData.entries())
        }
    }
}

class HomeViewModelFactory(
    private val repository: MoodRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(repository) as T
}
