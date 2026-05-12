package com.example.moodwheel.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class AddMoodUiState(
    val step: Int = 1,
    val moodLevel: MoodLevel? = null,
    val selectedMacro: MacroEmotion? = null,
    val selectedMacroIds: Set<String> = emptySet(),
    val selectedMicro: Set<String> = emptySet(),
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now().withSecond(0).withNano(0),
    val note: String = "",
    val isSaving: Boolean = false
) {
    val canGoNext: Boolean
        get() = when (step) {
            1 -> moodLevel != null
            2 -> selectedMacroIds.isNotEmpty()
            else -> true
        }
}

class AddMoodViewModel(
    private val repository: MoodRepository,
    initialDate: LocalDate? = null,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddMoodUiState(date = initialDate ?: LocalDate.now()))
    val uiState: StateFlow<AddMoodUiState> = _uiState.asStateFlow()

    fun selectMood(level: MoodLevel) {
        _uiState.update { it.copy(moodLevel = level) }
    }

    fun toggleMacro(emotion: MacroEmotion) {
        _uiState.update { current ->
            val selected = current.selectedMacroIds.toMutableSet()
            val wasSelected = !selected.add(emotion.id)
            if (wasSelected) selected.remove(emotion.id)

            val allowedMicro = EmotionCatalog.emotions
                .filter { it.id in selected }
                .flatMap { it.microEmotions }
                .toSet()
            val nextActive = when {
                !wasSelected -> emotion
                current.selectedMacro?.id != emotion.id -> current.selectedMacro
                else -> selected.firstOrNull()?.let(EmotionCatalog::byId)
            }

            current.copy(
                selectedMacro = nextActive,
                selectedMacroIds = selected,
                selectedMicro = current.selectedMicro.intersect(allowedMicro)
            )
        }
    }

    fun selectMacro(emotion: MacroEmotion) = toggleMacro(emotion)

    fun toggleMicro(label: String) {
        _uiState.update { current ->
            val next = current.selectedMicro.toMutableSet()
            if (!next.add(label)) next.remove(label)
            current.copy(selectedMicro = next)
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { it.copy(time = time.withSecond(0).withNano(0)) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note.take(300)) }
    }

    fun nextStep() {
        _uiState.update { current ->
            if (current.canGoNext) current.copy(step = (current.step + 1).coerceAtMost(4)) else current
        }
    }

    fun previousStep() {
        _uiState.update { current -> current.copy(step = (current.step - 1).coerceAtLeast(1)) }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val mood = state.moodLevel ?: return
        val macros = state.selectedMacroIds.map(EmotionCatalog::byId).ifEmpty { return }
        val macro = state.selectedMacro?.takeIf { active -> active.id in state.selectedMacroIds } ?: macros.first()
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val timestamp = LocalDateTime.of(state.date, state.time)
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()
            repository.add(
                MoodEntry(
                    timestamp = timestamp,
                    moodLevel = mood,
                    primaryEmotion = macro,
                    primaryEmotions = macros,
                    secondaryEmotions = state.selectedMicro.toList(),
                    note = state.note.trim()
                )
            )
            _uiState.value = AddMoodUiState()
            onSaved()
        }
    }
}

class AddMoodViewModelFactory(
    private val repository: MoodRepository,
    private val initialDate: LocalDate? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddMoodViewModel(repository, initialDate = initialDate) as T
}
