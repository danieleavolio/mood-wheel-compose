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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodEntry
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.components.MoodOrb
import com.example.moodwheel.ui.components.ProfileAvatar
import com.example.moodwheel.ui.theme.color
import com.example.moodwheel.ui.theme.softColor
import java.time.LocalDate

@Composable
fun HomeScreen(
    state: HomeUiState,
    profileName: String,
    avatarPath: String?,
    onAddMood: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeHeader(
                latest = state.latest,
                profileName = profileName,
                avatarPath = avatarPath
            )
            GradientButton(
                text = "Nuovo check-in",
                onClick = onAddMood,
                modifier = Modifier.fillMaxWidth()
            )
            LastEntryCard(entry = state.latest)
            WeekSummary(entries = state.allEntries, onDayClick = onDayClick)
            PrinciplesCard()
        }
    }
}

@Composable
private fun PrinciplesCard() {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                LocalDataMark()
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("I tuoi dati sono al sicuro", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Nessun account, nessun cloud. Solo tu e il tuo spazio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LocalDataMark() {
    val ink = MaterialTheme.colorScheme.tertiary
    Canvas(modifier = Modifier.size(22.dp)) {
        val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.82f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.58f)
            quadraticBezierTo(w * 0.68f, h * 0.8f, w * 0.5f, h * 0.92f)
            quadraticBezierTo(w * 0.32f, h * 0.8f, w * 0.24f, h * 0.58f)
            lineTo(w * 0.18f, h * 0.22f)
            close()
        }
        drawPath(shield, ink.copy(alpha = 0.86f), style = stroke)
        drawLine(ink, Offset(w * 0.38f, h * 0.5f), Offset(w * 0.48f, h * 0.62f), stroke.width, cap = StrokeCap.Round)
        drawLine(ink, Offset(w * 0.48f, h * 0.62f), Offset(w * 0.66f, h * 0.4f), stroke.width, cap = StrokeCap.Round)
    }
}

@Composable
private fun HomeHeader(
    latest: MoodEntry?,
    profileName: String,
    avatarPath: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.58f),
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.40f)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                "Ciao, ${profileName.trim().ifBlank { "Daniele" }}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (latest == null) {
                    "Come ti senti oggi?"
                } else {
                    "Come ti senti adesso?"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.52f)),
            contentAlignment = Alignment.Center
        ) {
            ProfileAvatar(
                name = profileName,
                avatarPath = avatarPath,
                size = 48.dp
            )
        }
    }
}

@Composable
private fun LastEntryCard(entry: MoodEntry?) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Ultimo check-in", style = MaterialTheme.typography.titleMedium)
            if (entry == null) {
                Text(
                    "Ancora nessun momento registrato. Puoi iniziare con un check-in leggero.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MoodOrb(level = entry.moodLevel, size = 52.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                entry.primaryEmotions.joinToString(" + ") { it.label },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                entry.timestamp.formatTime(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (entry.primaryEmotions.map { it.label } + entry.secondaryEmotions)
                                .distinct()
                                .take(2)
                                .forEach { label ->
                                    MoodTag(label = label, color = entry.primaryEmotion.softColor())
                                }
                        }
                        if (entry.note.isNotBlank()) {
                            Text(
                                text = entry.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekSummary(
    entries: List<MoodEntry>,
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val byDay = entries.groupBy { it.timestamp.toLocalDate() }

    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Panoramica settimana", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${entries.count { !it.timestamp.toLocalDate().isBefore(today.minusDays(6)) }} check-in registrati",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "7 giorni",
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    val hasEntries = byDay[day].orEmpty().isNotEmpty()
                    val dominant = byDay[day]
                        ?.flatMap { it.primaryEmotions }
                        ?.groupingBy { it.id }
                        ?.eachCount()
                        ?.maxByOrNull { it.value }
                        ?.key
                        ?.let(EmotionCatalog::byId)
                    val isToday = day == today
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(if (isToday) 34.dp else 28.dp)
                                .clip(CircleShape)
                                .clickable { onDayClick(day) }
                                .background(
                                    when {
                                        dominant != null -> dominant.color().copy(alpha = if (isToday) 0.86f else 0.62f)
                                        hasEntries -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (dominant != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            day.dayOfWeek.name.first().toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodTag(label: String, color: Color) {
    Box(
        modifier = Modifier
            .heightIn(min = 24.dp)
            .widthIn(max = 96.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = label.lowercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
