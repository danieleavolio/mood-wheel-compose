package com.example.moodwheel.ui.screens

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.domain.export.EntryJsonExporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExportViewModel(
    private val repository: MoodRepository,
    private val exporter: EntryJsonExporter = EntryJsonExporter()
) : ViewModel() {
    val entries = repository.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun exportTo(contentResolver: ContentResolver, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                val json = exporter.export(entries.value)
                contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray(Charsets.UTF_8))
                } ?: error("Output stream unavailable")
            }.onSuccess {
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }
}

class ExportViewModelFactory(
    private val repository: MoodRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ExportViewModel(repository) as T
}
