package com.example.moodwheel.ui.screens

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.domain.export.EntryJsonExporter
import com.example.moodwheel.domain.export.EntryJsonImporter
import com.example.moodwheel.domain.model.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ExportViewModel(
    private val repository: MoodRepository,
    private val exporter: EntryJsonExporter = EntryJsonExporter(),
    private val importer: EntryJsonImporter = EntryJsonImporter()
) : ViewModel() {
    val entries = repository.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun exportTo(contentResolver: ContentResolver, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val json = exporter.export(entries.value)
                    contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: error("Output stream unavailable")
                }
            }.onSuccess {
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun importFrom(contentResolver: ContentResolver, uri: Uri, onComplete: (ImportResult) -> Unit) {
        viewModelScope.launch {
            runCatching {
                val json = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: error("Input stream unavailable")
                }
                val parsed = importer.import(json)
                val existing = entries.value.map { it.signature() }.toMutableSet()
                val unique = parsed
                    .distinctBy { it.signature() }
                    .filter { existing.add(it.signature()) }

                unique.forEach { repository.add(it.copy(id = 0)) }
                ImportResult(imported = unique.size, skipped = parsed.size - unique.size, success = true)
            }.onSuccess(onComplete).onFailure {
                onComplete(ImportResult(imported = 0, skipped = 0, success = false))
            }
        }
    }

    fun saveAvatar(context: Context, uri: Uri, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val target = File(context.filesDir, "profile_avatar")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(target).use { output -> input.copyTo(output) }
                    } ?: error("Avatar stream unavailable")
                    target.absolutePath
                }
            }.onSuccess(onComplete).onFailure {
                onComplete(null)
            }
        }
    }
}

data class ImportResult(
    val imported: Int,
    val skipped: Int,
    val success: Boolean
)

private fun MoodEntry.signature(): String =
    listOf(
        timestamp.toString(),
        moodLevel.value.toString(),
        primaryEmotion.id,
        secondaryEmotions.sorted().joinToString("|"),
        note.trim()
    ).joinToString("#")

class ExportViewModelFactory(
    private val repository: MoodRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ExportViewModel(repository) as T
}
