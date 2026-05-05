package com.example.moodwheel.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodwheel.R
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.AppIllustration
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.components.GradientButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryScreen(
    entries: List<MoodEntry>,
    onEntryClick: (MoodEntry) -> Unit,
    onAddMood: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEmotionId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val normalizedQuery = query.trim().lowercase()
    val filteredEntries = entries.filter { entry ->
        val matchesEmotion = selectedEmotionId == null || entry.primaryEmotion.id == selectedEmotionId
        val searchable = buildString {
            append(entry.primaryEmotion.label)
            append(" ")
            append(entry.secondaryEmotions.joinToString(" "))
            append(" ")
            append(entry.note)
        }.lowercase()
        val matchesQuery = normalizedQuery.isBlank() || searchable.contains(normalizedQuery)
        matchesEmotion && matchesQuery
    }
    val grouped = filteredEntries.groupBy { it.timestamp.toLocalDate() }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Diario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            item {
                CalmCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Cerca note o emozioni") }
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedEmotionId == null,
                                onClick = { selectedEmotionId = null },
                                label = { Text("Tutte") }
                            )
                            EmotionCatalog.emotions.forEach { emotion ->
                                FilterChip(
                                    selected = selectedEmotionId == emotion.id,
                                    onClick = { selectedEmotionId = emotion.id },
                                    label = { Text(emotion.label) }
                                )
                            }
                        }
                    }
                }
            }

            if (filteredEntries.isEmpty()) {
                item {
                    CalmCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppIllustration(resId = R.drawable.empty_state)
                            Text("Nessun momento trovato", fontWeight = FontWeight.SemiBold)
                            Text("Puoi cambiare ricerca o aggiungere un nuovo check-in.")
                            GradientButton("Aggiungi umore", onClick = onAddMood, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            } else {
                grouped.forEach { (date, dayEntries) ->
                    item {
                        Text(dayTitle(date), fontWeight = FontWeight.SemiBold)
                    }
                    items(dayEntries, key = { it.id }) { entry ->
                        DiaryEntryRow(entry = entry, onClick = { onEntryClick(entry) })
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryEntryRow(
    entry: MoodEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CalmCard(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmotionArtwork(emotion = entry.primaryEmotion, size = 54.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(entry.primaryEmotion.label, fontWeight = FontWeight.Bold)
                Text(entry.secondaryEmotions.take(3).joinToString(", ").ifBlank { "Solo categoria" })
                if (entry.note.isNotBlank()) {
                    Text(entry.note, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(entry.timestamp.formatTime(), style = MaterialTheme.typography.labelMedium)
        }
    }
}
