package com.example.moodwheel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.R
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.ui.components.AppIllustration
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entriesByDay = state.monthEntries.groupBy { it.timestamp.toLocalDate() }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) { Text("<") }
                Text(
                    text = state.visibleMonth.formatMonth(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onNextMonth) { Text(">") }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppIllustration(resId = R.drawable.calendar_mood)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("L", "M", "M", "G", "V", "S", "D").forEach {
                            Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }

                    CalendarGrid(
                        month = state.visibleMonth,
                        entriesByDay = entriesByDay
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmotionCatalog.emotions.forEach { emotion ->
                    LegendItem(emotion)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )
                    Text("Neutro / Misto", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    entriesByDay: Map<LocalDate, List<com.example.moodwheel.domain.model.MoodEntry>>
) {
    val daysInMonth = month.lengthOfMonth()
    val firstWeekday = month.dayOfWeek.value
    val leadingEmptyDays = List(firstWeekday - 1) { null as Int? }
    val monthDays = (1..daysInMonth).map { it as Int? }
    val cells = leadingEmptyDays + monthDays

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    val date = day?.let { month.withDayOfMonth(it) }
                    val emotion = date?.let { dominantEmotion(entriesByDay[it].orEmpty()) }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(emotion?.color() ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                    .border(
                                        width = if (date == LocalDate.now()) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(day.toString(), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
                repeat(7 - week.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LegendItem(emotion: MacroEmotion) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(emotion.color())
        )
        Text(emotion.label, style = MaterialTheme.typography.labelMedium)
    }
}

private fun dominantEmotion(entries: List<com.example.moodwheel.domain.model.MoodEntry>): MacroEmotion? =
    entries
        .groupingBy { it.primaryEmotion.id }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?.let(EmotionCatalog::byId)
