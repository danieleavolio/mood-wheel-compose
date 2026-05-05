package com.example.moodwheel.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.moodwheel.ui.components.MoodOrb
import com.example.moodwheel.ui.theme.color
import java.time.LocalDate

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAddMood: () -> Unit,
    modifier: Modifier = Modifier
) {
    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            HomeHeader(state.latest)
            LastEntryCard(entry = state.latest)
            WeekSummary(entries = state.allEntries)
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EmotionArtwork(emotion = null, size = 72.dp)
                    Column {
                        Text("Una nota gentile", fontWeight = FontWeight.SemiBold)
                        Text("Non devi spiegare tutto. Anche un check-in piccolo va bene.")
                    }
                }
            }
            GradientButton(
                text = "Aggiungi umore",
                onClick = onAddMood,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HomeHeader(latest: MoodEntry?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6A54F5), Color(0xFF9B8CFF), Color(0xFFFFB38D))
                )
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Ciao, Daniele",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (latest == null) {
                    "Come ti senti oggi?"
                } else {
                    "Come ti senti adesso?"
                },
                color = Color.White.copy(alpha = 0.86f)
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Text("D", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LastEntryCard(entry: MoodEntry?) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ultimo check-in", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            if (entry == null) {
                AppIllustration(resId = R.drawable.empty_state)
                Text("Nessun momento registrato ancora.")
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MoodOrb(level = entry.moodLevel, size = 56.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(entry.primaryEmotion.label, fontWeight = FontWeight.Bold)
                        Text(entry.secondaryEmotions.joinToString(", ").ifBlank { "Solo categoria" })
                        Text("Oggi, ${entry.timestamp.formatTime()}", style = MaterialTheme.typography.labelMedium)
                    }
                    Text(">", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AnimatedVisibility(visible = entry.note.isNotBlank(), enter = fadeIn()) {
                    Text(
                        text = entry.note,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Panoramica settimana", fontWeight = FontWeight.SemiBold)
                Text(
                    "${entries.count { !it.timestamp.toLocalDate().isBefore(today.minusDays(6)) }} momenti registrati",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(if (day == today) 30.dp else 24.dp)
                                .clip(CircleShape)
                                .background(dominant?.color() ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dominant == null) {
                                Text(" ", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text(day.dayOfWeek.name.first().toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
