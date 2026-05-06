package com.example.moodwheel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CalendarHeader(
                month = state.visibleMonth.formatMonth(),
                moments = state.monthEntries.size,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("L", "M", "M", "G", "V", "S", "D").forEach {
                            Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }

                    CalendarGrid(
                        month = state.visibleMonth,
                        entriesByDay = entriesByDay,
                        onDayClick = { selectedDate = it }
                    )
                }
            }

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                FlowRow(
                    modifier = Modifier.padding(16.dp),
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

    selectedDate?.let { date ->
        val dayEntries = entriesByDay[date].orEmpty()
        ModalBottomSheet(onDismissRequest = { selectedDate = null }) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(dayTitle(date), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (dayEntries.isEmpty()) {
                    Text("Nessun momento registrato in questo giorno.")
                } else {
                    dayEntries.forEach { entry ->
                        DiaryEntryRow(
                            entry = entry,
                            onClick = {
                                selectedDate = null
                                onEntryClick(entry)
                            }
                        )
                    }
                }
                GradientButton(
                    text = "Aggiungi qui",
                    onClick = {
                        selectedDate = null
                        onAddMoodForDate(date)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
        MonthButton("<", onPreviousMonth)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(month, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("$moments momenti", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
        MonthButton(">", onNextMonth)
    }
}

@Composable
private fun MonthButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFFCF8),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    entriesByDay: Map<LocalDate, List<MoodEntry>>,
    onDayClick: (LocalDate) -> Unit
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
                    val dayEntries = date?.let { entriesByDay[it].orEmpty() }.orEmpty()
                    val emotion = dominantEmotion(dayEntries)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            CalendarDayCell(
                                day = day,
                                date = date,
                                emotion = emotion,
                                count = dayEntries.size,
                                onClick = { date?.let(onDayClick) }
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
    date: LocalDate?,
    emotion: MacroEmotion?,
    count: Int,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()
    val background = emotion?.color()?.copy(alpha = 0.78f)
        ?: if (isToday) Color(0xFFECE7FF) else Color.Transparent
    val textColor = if (emotion == null) MaterialTheme.colorScheme.onSurface else Color(0xFF1F1A28)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day.toString(), style = MaterialTheme.typography.labelLarge, color = textColor, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
        if (count > 1) {
            Text("$count", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.72f))
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
