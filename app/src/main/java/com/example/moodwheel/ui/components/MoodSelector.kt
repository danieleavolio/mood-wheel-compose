package com.example.moodwheel.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun MoodListSelector(
    selected: MoodLevel?,
    onSelect: (MoodLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MoodLevel.displayOrder.forEach { level ->
            MoodListChoice(
                level = level,
                selected = selected == level,
                onClick = { onSelect(level) }
            )
        }
    }
}

@Composable
private fun MoodListChoice(
    level: MoodLevel,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (selected) levelColor(level).copy(alpha = 0.14f) else Color.Transparent,
        label = "moodRowBackground"
    )
    val orbSize by animateDpAsState(if (selected) 62.dp else 44.dp, label = "moodRowOrb")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(background, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoodOrb(level = level, size = orbSize, selected = selected)
        Spacer(Modifier.width(16.dp))
        Text(
            text = level.label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("OK", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
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
    val size by animateDpAsState(if (selected) 72.dp else 54.dp, label = "moodSize")
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = if (selected) 3.dp else 0.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            MoodOrb(level = level, size = size, selected = selected)
        }
        Text(
            text = level.label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
