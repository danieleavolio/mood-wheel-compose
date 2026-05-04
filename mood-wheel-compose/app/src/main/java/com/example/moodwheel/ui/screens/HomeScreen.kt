package com.example.moodwheel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAddMood: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Ciao!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Come ti senti oggi?", style = MaterialTheme.typography.bodyLarge)

        LastEntryCard(entry = state.latest)
        WeekSummary(entries = state.allEntries)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Ricorda", fontWeight = FontWeight.SemiBold)
                Text("Ogni emozione è valida. Ascoltati, senza giudizio.")
            }
        }

        Button(
            onClick = onAddMood,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("+  Aggiungi umore")
        }
    }
}

@Composable
private fun LastEntryCard(entry: MoodEntry?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Ultimo check-in", style = MaterialTheme.typography.labelLarge)
            if (entry == null) {
                Text("Nessun momento registrato ancora.")
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(entry.primaryEmotion.color()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(entry.moodLevel.face, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(entry.primaryEmotion.label, fontWeight = FontWeight.Bold)
                        Text(entry.secondaryEmotions.joinToString(", ").ifBlank { "Solo categoria" })
                        Text("Oggi, ${entry.timestamp.formatTime()}", style = MaterialTheme.typography.labelMedium)
                    }
                    Text(">")
                }
            }
        }
    }
}

@Composable
private fun WeekSummary(entries: List<MoodEntry>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val byDay = entries.groupBy { it.timestamp.toLocalDate() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Panoramica settimanale", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    val dominant = byDay[day]
                        ?.groupingBy { it.primaryEmotion.id }
                        ?.eachCount()
                        ?.maxByOrNull { it.value }
                        ?.key
                        ?.let(EmotionCatalog::byId)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(dominant?.color() ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        )
                        Text(day.dayOfWeek.name.first().toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
