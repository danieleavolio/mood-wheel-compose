package com.example.moodwheel.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@Composable
fun StatsScreen(
    entries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val weekStart = LocalDate.now().minusDays(6)
    val weekEntries = entries.filter { !it.timestamp.toLocalDate().isBefore(weekStart) }
    val prevalent = weekEntries
        .groupingBy { it.primaryEmotion.id }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?.let { id -> weekEntries.first { it.primaryEmotion.id == id }.primaryEmotion }
    val mostUsedWords = weekEntries
        .flatMap { it.secondaryEmotions }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(3)
        .joinToString(", ") { it.key }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Statistiche", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                RangePill("Settimana", selected = true, modifier = Modifier.weight(1f))
                RangePill("Mese", selected = false, modifier = Modifier.weight(1f))
                RangePill("Anno", selected = false, modifier = Modifier.weight(1f))
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    MetricBlock("Momenti", weekEntries.size.toString(), Modifier.weight(1f))
                    MetricBlock(
                        "Frequenza",
                        if (weekEntries.isEmpty()) "0%" else "${(weekEntries.size * 100 / 7).coerceAtMost(100)}%",
                        Modifier.weight(1f)
                    )
                    MetricBlock(
                        "Giorni",
                        weekEntries.map { it.timestamp.toLocalDate() }.distinct().size.toString(),
                        Modifier.weight(1f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(title = "Parole", value = weekEntries.flatMap { it.secondaryEmotions }.distinct().size.toString(), modifier = Modifier.weight(1f))
                StatCard(
                    title = "Note",
                    value = weekEntries.count { it.note.isNotBlank() }.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Emozione prevalente", fontWeight = FontWeight.SemiBold)
                    if (prevalent == null) {
                        Text("Non ci sono ancora abbastanza momenti questa settimana.")
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            EmotionArtwork(emotion = prevalent, size = 58.dp)
                            Column {
                                Text(prevalent.label, color = prevalent.color(), fontWeight = FontWeight.Bold)
                                Text("${weekEntries.count { it.primaryEmotion.id == prevalent.id }} momenti")
                            }
                        }
                    }
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Andamento leggero", fontWeight = FontWeight.SemiBold)
                    MoodTrend(entries = weekEntries)
                    Text("Uno sguardo semplice, senza giudizi o punteggi.")
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Parole ricorrenti", fontWeight = FontWeight.SemiBold)
                    Text(if (mostUsedWords.isBlank()) "Ancora nessuna parola specifica." else mostUsedWords)
                }
            }
        }
    }
}

@Composable
private fun RangePill(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    CalmCard(modifier = modifier) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MetricBlock(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    CalmCard(
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title)
        }
    }
}

@Composable
private fun MoodTrend(entries: List<MoodEntry>) {
    val points = entries
        .sortedBy { it.timestamp }
        .takeLast(8)
        .map { it.moodLevel.value }
    val chartBackground = MaterialTheme.colorScheme.surfaceVariant
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(chartBackground, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        if (points.size < 2) return@Canvas
        val step = size.width / (points.size - 1)
        val mapped = points.mapIndexed { index, value ->
            Offset(
                x = index * step,
                y = size.height - ((value - 1) / 4f) * size.height
            )
        }
        mapped.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = 5f
            )
        }
        mapped.forEach {
            drawCircle(lineColor, radius = 6f, center = it)
        }
    }
}
