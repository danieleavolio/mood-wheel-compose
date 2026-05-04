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
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MoodEntry
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Statistiche", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(title = "Momenti", value = weekEntries.size.toString(), modifier = Modifier.weight(1f))
            StatCard(
                title = "Giorni",
                value = weekEntries.map { it.timestamp.toLocalDate() }.distinct().size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Emozione prevalente", fontWeight = FontWeight.SemiBold)
                if (prevalent == null) {
                    Text("Non ci sono ancora abbastanza momenti questa settimana.")
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(prevalent.label, color = prevalent.color(), fontWeight = FontWeight.Bold)
                        Text("${weekEntries.count { it.primaryEmotion.id == prevalent.id }} momenti")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Andamento leggero", fontWeight = FontWeight.SemiBold)
                MoodTrend(entries = weekEntries)
                Text("Uno sguardo semplice, senza giudizi o punteggi.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Parole ricorrenti", fontWeight = FontWeight.SemiBold)
                Text(if (mostUsedWords.isBlank()) "Ancora nessuna parola specifica." else mostUsedWords)
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
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
                color = Color(0xFF5D4AE3),
                start = start,
                end = end,
                strokeWidth = 5f
            )
        }
        mapped.forEach {
            drawCircle(Color(0xFF5D4AE3), radius = 6f, center = it)
        }
    }
}
