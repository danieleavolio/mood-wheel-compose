package com.example.moodwheel.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
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

    BackHandler(onBack = onBack)

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
                TextButton(onClick = onBack) { Text("<") }
                Text(
                    "Dettaglio momento",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = { showDeleteDialog = true }) {
                    Text("Elimina", color = Color(0xFFE45252))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(selectedMacro.softColor(), MaterialTheme.colorScheme.surface)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmotionArtwork(emotion = selectedMacro, size = 110.dp)
                    Text(selectedMacro.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${date.formatDate()} - %02d:%02d".format(time.hour, time.minute))
                    Text(
                        selectedMicro.joinToString(", ").ifBlank { "Solo categoria" },
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    DetailPill(
                        label = date.formatDate(),
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day -> date = LocalDate.of(year, month + 1, day) },
                                date.year,
                                date.monthValue - 1,
                                date.dayOfMonth
                            ).show()
                        }
                    )
                    DetailPill(
                        label = "%02d:%02d".format(time.hour, time.minute),
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute -> time = LocalTime.of(hour, minute) },
                                time.hour,
                                time.minute,
                                true
                            ).show()
                        }
                    )
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Nota", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(300) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        minLines = 5,
                        placeholder = { Text("Aggiungi o modifica una nota.") },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
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

@Composable
private fun DetailPill(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}

private fun Long.toLocalTime(): LocalTime =
    java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0)
