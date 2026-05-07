package com.example.moodwheel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.MoodOrb
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onEntryClick: (MoodEntry) -> Unit,
    onAddMoodForDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val entriesByDay = state.monthEntries.groupBy { it.timestamp.toLocalDate() }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }

    LaunchedEffect(state.visibleMonth) {
        selectedDate = if (
            today.year == state.visibleMonth.year &&
            today.month == state.visibleMonth.month
        ) {
            today
        } else {
            state.visibleMonth
        }
    }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalendarHeader(
                month = state.visibleMonth.formatMonth(),
                moments = state.monthEntries.size,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            CalendarMonthCard(
                month = state.visibleMonth,
                selectedDate = selectedDate,
                entriesByDay = entriesByDay,
                onDayClick = { selectedDate = it }
            )

            SelectedDayCard(
                date = selectedDate,
                entries = entriesByDay[selectedDate].orEmpty(),
                onEntryClick = onEntryClick,
                onAddMoodForDate = onAddMoodForDate
            )

            CalendarLegend()
        }
    }
}

@Composable
private fun CalendarHeader(
    month: String,
    moments: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonthButton(label = "<", onClick = onPreviousMonth, description = "Mese precedente")
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(month, style = MaterialTheme.typography.titleLarge)
            Text(
                "$moments check-in",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
        MonthButton(label = ">", onClick = onNextMonth, description = "Mese successivo")
    }
}

@Composable
private fun MonthButton(
    label: String,
    onClick: () -> Unit,
    description: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .semantics { contentDescription = description },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.58f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 1.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CalendarMonthCard(
    month: LocalDate,
    selectedDate: LocalDate,
    entriesByDay: Map<LocalDate, List<MoodEntry>>,
    onDayClick: (LocalDate) -> Unit
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("L", "M", "M", "G", "V", "S", "D").forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            CalendarGrid(
                month = month,
                selectedDate = selectedDate,
                entriesByDay = entriesByDay,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    entriesByDay: Map<LocalDate, List<MoodEntry>>,
    onDayClick: (LocalDate) -> Unit
) {
    val daysInMonth = month.lengthOfMonth()
    val firstWeekday = month.dayOfWeek.value
    val leadingEmptyDays = List(firstWeekday - 1) { null as Int? }
    val monthDays = (1..daysInMonth).map { it as Int? }
    val cells = leadingEmptyDays + monthDays

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    val date = day?.let { month.withDayOfMonth(it) }
                    val dayEntries = date?.let { entriesByDay[it].orEmpty() }.orEmpty()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            CalendarDayCell(
                                day = day,
                                date = date,
                                emotion = dominantEmotion(dayEntries),
                                hasEntries = dayEntries.isNotEmpty(),
                                selected = date == selectedDate,
                                onClick = { onDayClick(date) }
                            )
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
private fun CalendarDayCell(
    day: Int,
    date: LocalDate,
    emotion: MacroEmotion?,
    hasEntries: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()
    val cellBackground = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
        else -> Color.Transparent
    }
    val dotColor = emotion?.color() ?: MaterialTheme.colorScheme.outline

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(cellBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected || isToday) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (hasEntries) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
private fun SelectedDayCard(
    date: LocalDate,
    entries: List<MoodEntry>,
    onEntryClick: (MoodEntry) -> Unit,
    onAddMoodForDate: (LocalDate) -> Unit
) {
    val latest = entries.maxByOrNull { it.timestamp }

    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (latest != null) {
                MoodOrb(level = latest.moodLevel, size = 46.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(dayTitle(date), style = MaterialTheme.typography.titleMedium)
                if (latest == null) {
                    Text(
                        "Nessun check-in in questo giorno.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        "${latest.primaryEmotion.label} alle ${latest.timestamp.formatTime()}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        latest.note.ifBlank { "${entries.size} momenti registrati" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            FilledTonalButton(
                onClick = {
                    if (latest == null) {
                        onAddMoodForDate(date)
                    } else {
                        onEntryClick(latest)
                    }
                },
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(if (latest == null) "Aggiungi" else "Apri", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalendarLegend() {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmotionCatalog.emotions.forEach { emotion ->
                LegendItem(emotion)
            }
        }
    }
}

@Composable
private fun LegendItem(emotion: MacroEmotion) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(emotion.color())
        )
        Text(
            emotion.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun dominantEmotion(entries: List<MoodEntry>): MacroEmotion? =
    entries
        .groupingBy { it.primaryEmotion.id }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?.let(EmotionCatalog::byId)
