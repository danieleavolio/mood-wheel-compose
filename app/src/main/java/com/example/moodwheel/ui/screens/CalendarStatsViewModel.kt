package com.example.moodwheel.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.domain.model.MoodEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class CalendarUiState(
    val visibleMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val monthEntries: List<MoodEntry> = emptyList()
)

class CalendarStatsViewModel(
    private val repository: MoodRepository
) : ViewModel() {
    private val visibleMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    @OptIn(ExperimentalCoroutinesApi::class)
    val calendarUiState: StateFlow<CalendarUiState> =
        combine(
            visibleMonth,
            visibleMonth.flatMapLatest { month -> repository.observeMonth(month) }
        ) { month, entries ->
            CalendarUiState(visibleMonth = month, monthEntries = entries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarUiState()
        )

    val allEntries: StateFlow<List<MoodEntry>> =
        repository.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun previousMonth() {
        visibleMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        visibleMonth.update { it.plusMonths(1) }
    }
}

class CalendarStatsViewModelFactory(
    private val repository: MoodRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CalendarStatsViewModel(repository) as T
}
