package com.example.moodwheel.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MoodLevel

@Composable
fun MoodSelector(
    selected: MoodLevel?,
    onSelect: (MoodLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        MoodLevel.displayOrder.forEach { level ->
            MoodChoice(
                level = level,
                selected = selected == level,
                onClick = { onSelect(level) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MoodChoice(
    level: MoodLevel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val size by animateDpAsState(if (selected) 72.dp else 52.dp, label = "moodSize")
    val color by animateColorAsState(levelColor(level), label = "moodColor")

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(color, CircleShape)
                .border(
                    width = if (selected) 4.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.face,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = level.label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

private fun levelColor(level: MoodLevel): Color =
    when (level) {
        MoodLevel.VeryGood -> Color(0xFF6CC35B)
        MoodLevel.Good -> Color(0xFFA8D66D)
        MoodLevel.Neutral -> Color(0xFFFFD45A)
        MoodLevel.Bad -> Color(0xFFFFA14D)
        MoodLevel.VeryBad -> Color(0xFFFF7468)
    }
