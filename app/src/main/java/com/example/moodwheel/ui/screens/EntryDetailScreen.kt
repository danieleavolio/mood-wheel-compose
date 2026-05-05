package com.example.moodwheel.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.components.MoodSelector

@Composable
fun EntryDetailScreen(
    entry: MoodEntry,
    onBack: () -> Unit,
    onSave: (MoodEntry) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var moodLevel by remember(entry.id) { mutableStateOf(entry.moodLevel) }
    var note by remember(entry.id) { mutableStateOf(entry.note) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onBack) { Text("Indietro") }
                Text(
                    "Dettaglio momento",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmotionArtwork(emotion = entry.primaryEmotion, size = 110.dp)
                    Text(entry.primaryEmotion.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${entry.timestamp.toLocalDate().formatDate()} · ${entry.timestamp.formatTime()}")
                    Text(entry.secondaryEmotions.joinToString(", ").ifBlank { "Solo categoria" })
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Come lo descriveresti ora?", fontWeight = FontWeight.SemiBold)
                    MoodSelector(selected = moodLevel, onSelect = { moodLevel = it })
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Nota", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(300) },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        minLines = 5,
                        placeholder = { Text("Aggiungi o modifica una nota.") }
                    )
                }
            }

            GradientButton(
                text = "Salva modifiche",
                onClick = {
                    onSave(entry.copy(moodLevel = moodLevel, note = note.trim()))
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Elimina momento")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminare questo momento?") },
            text = { Text("Questa azione rimuove solo questo check-in dal telefono.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(entry.id)
                        onBack()
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}
