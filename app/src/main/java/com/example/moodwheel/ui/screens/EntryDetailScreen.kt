package com.example.moodwheel.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.domain.model.MoodLevel
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.components.EmotionChips
import com.example.moodwheel.ui.components.EmotionWheel
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.components.MoodSelector
import com.example.moodwheel.ui.theme.softColor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun EntryDetailScreen(
    entry: MoodEntry,
    onBack: () -> Unit,
    onSave: (MoodEntry) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var moodLevel by remember(entry.id) { mutableStateOf(entry.moodLevel) }
    var selectedMacro by remember(entry.id) { mutableStateOf(entry.primaryEmotion) }
    var selectedMicro by remember(entry.id) { mutableStateOf(entry.secondaryEmotions.toSet()) }
    var date by remember(entry.id) { mutableStateOf(entry.timestamp.toLocalDate()) }
    var time by remember(entry.id) { mutableStateOf(entry.timestamp.toLocalTime()) }
    var note by remember(entry.id) { mutableStateOf(entry.note) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    fun selectMacro(emotion: MacroEmotion) {
        selectedMacro = emotion
        selectedMicro = selectedMicro.intersect(emotion.microEmotions.toSet())
    }

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
                    EmotionArtwork(emotion = selectedMacro, size = 110.dp)
                    Text(selectedMacro.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${date.formatDate()} · %02d:%02d".format(time.hour, time.minute))
                    Text(selectedMicro.joinToString(", ").ifBlank { "Solo categoria" })
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
                    Text("Emozione", fontWeight = FontWeight.SemiBold)
                    EmotionWheel(selected = selectedMacro, onSelect = ::selectMacro)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(selectedMacro.softColor(), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(selectedMacro.label, fontWeight = FontWeight.SemiBold)
                        EmotionChips(
                            emotion = selectedMacro,
                            selected = selectedMicro,
                            onToggle = { label ->
                                selectedMicro = selectedMicro.toMutableSet().also { set ->
                                    if (!set.add(label)) set.remove(label)
                                }
                            }
                        )
                    }
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Data e ora", fontWeight = FontWeight.SemiBold)
                    OutlinedButton(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day -> date = LocalDate.of(year, month + 1, day) },
                                date.year,
                                date.monthValue - 1,
                                date.dayOfMonth
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(date.formatDate())
                    }
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute -> time = LocalTime.of(hour, minute) },
                                time.hour,
                                time.minute,
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("%02d:%02d".format(time.hour, time.minute))
                    }
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
                    val timestamp = LocalDateTime.of(date, time)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    onSave(
                        entry.copy(
                            timestamp = timestamp,
                            moodLevel = moodLevel,
                            primaryEmotion = selectedMacro,
                            secondaryEmotions = selectedMicro.toList(),
                            note = note.trim()
                        )
                    )
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

private fun Long.toLocalTime(): LocalTime =
    java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0)
