package com.example.moodwheel.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class StatsRange(val label: String) {
    Week("Settimana"),
    Month("Mese"),
    Year("Anno")
}

@Composable
fun StatsScreen(
    entries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    var range by remember { mutableStateOf(StatsRange.Week) }
    val today = LocalDate.now()
    val start = when (range) {
        StatsRange.Week -> today.minusDays(6)
        StatsRange.Month -> today.withDayOfMonth(1)
        StatsRange.Year -> today.withDayOfYear(1)
    }
    val rangeEntries = entries.filter { !it.timestamp.toLocalDate().isBefore(start) }
    val activeDays = rangeEntries.map { it.timestamp.toLocalDate() }.distinct().size
    val totalDays = when (range) {
        StatsRange.Week -> 7
        StatsRange.Month -> today.lengthOfMonth()
        StatsRange.Year -> today.lengthOfYear()
    }
    val words = rangeEntries.flatMap { it.secondaryEmotions }.distinct().size
    val notes = rangeEntries.count { it.note.isNotBlank() }
    val averageMood = rangeEntries.map { it.moodLevel.value }.average().takeIf { !it.isNaN() } ?: 0.0
    val distribution = emotionDistribution(rangeEntries)

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Statistiche", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        rangeLabel(range, start, today),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${rangeEntries.size} momenti",
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            RangeSegmentedControl(selected = range, onSelect = { range = it })

            MetricGrid(
                entriesCount = rangeEntries.size,
                frequency = if (totalDays == 0) 0 else (activeDays * 100 / totalDays).coerceAtMost(100),
                activeDays = activeDays,
                words = words,
                notes = notes,
                averageMood = averageMood
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmotionDistributionCard(
                    distribution = distribution,
                    total = rangeEntries.size,
                    modifier = Modifier.weight(1f)
                )
                MoodTrendCard(
                    entries = rangeEntries,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeSegmentedControl(
    selected: StatsRange,
    onSelect: (StatsRange) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f))
            .padding(4.dp)
    ) {
        StatsRange.entries.forEachIndexed { index, range ->
            val isSelected = selected == range
            SegmentedButton(
                selected = isSelected,
                onClick = { onSelect(range) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = StatsRange.entries.size),
                label = {
                    Text(
                        range.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = Color.Transparent,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    activeBorderColor = Color.Transparent,
                    inactiveBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun MetricGrid(
    entriesCount: Int,
    frequency: Int,
    activeDays: Int,
    words: Int,
    notes: Int,
    averageMood: Double
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("Momenti", entriesCount.toString(), Modifier.weight(1f))
                MetricTile("Frequenza", "$frequency%", Modifier.weight(1f))
                MetricTile("Giorni", activeDays.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("Parole", words.toString(), Modifier.weight(1f))
                MetricTile("Note", notes.toString(), Modifier.weight(1f))
                MetricTile("Media", if (averageMood == 0.0) "-" else "%.1f/5".format(averageMood), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.24f))
            .padding(horizontal = 10.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmotionDistributionCard(
    distribution: List<Pair<MacroEmotion, Int>>,
    total: Int,
    modifier: Modifier = Modifier
) {
    CalmCard(modifier = modifier.height(226.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Distribuzione", style = MaterialTheme.typography.titleMedium)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DonutChart(distribution = distribution, total = total)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(total.toString(), style = MaterialTheme.typography.titleLarge)
                    Text("check-in", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            distribution.take(3).forEach { (emotion, count) ->
                LegendLine(emotion = emotion, percent = if (total == 0) 0 else count * 100 / total)
            }
        }
    }
}

@Composable
private fun DonutChart(
    distribution: List<Pair<MacroEmotion, Int>>,
    total: Int
) {
    val fallback = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier = Modifier.height(86.dp).fillMaxWidth()) {
        val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
        val radius = 31.dp.toPx()
        val topLeft = Offset(size.width / 2f - radius, size.height / 2f - radius)
        val chartSize = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        if (distribution.isEmpty() || total == 0) {
            drawArc(fallback, -90f, 360f, false, topLeft, chartSize, style = stroke)
        } else {
            var start = -90f
            distribution.forEach { (emotion, count) ->
                val sweep = 360f * count / total
                drawArc(emotion.color(), start, sweep, false, topLeft, chartSize, style = stroke)
                start += sweep
            }
        }
    }
}

@Composable
private fun LegendLine(emotion: MacroEmotion, percent: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(emotion.color())
                .height(8.dp)
                .weight(0.25f)
        )
        Text(emotion.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
        Text("$percent%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MoodTrendCard(
    entries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    CalmCard(modifier = modifier.height(226.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Andamento", style = MaterialTheme.typography.titleMedium)
            MoodTrend(entries = entries)
            Text(
                "Semplice media dei check-in nel periodo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MoodTrend(entries: List<MoodEntry>) {
    val points = entries.sortedBy { it.timestamp }.takeLast(9).map { it.moodLevel.value }
    val chartBackground = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f)
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(chartBackground)
            .padding(12.dp)
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
            drawLine(lineColor, start, end, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        }
        mapped.forEach {
            drawCircle(lineColor, radius = 4.dp.toPx(), center = it)
        }
    }
}

private fun emotionDistribution(entries: List<MoodEntry>): List<Pair<MacroEmotion, Int>> =
    entries
        .groupingBy { it.primaryEmotion.id }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .map { (id, count) -> EmotionCatalog.byId(id) to count }

private fun rangeLabel(range: StatsRange, start: LocalDate, today: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN)
    return when (range) {
        StatsRange.Week -> "${start.format(formatter)} - ${today.format(formatter)}"
        StatsRange.Month -> today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN))
            .replaceFirstChar { it.titlecase(Locale.ITALIAN) }
        StatsRange.Year -> today.year.toString()
    }
}
