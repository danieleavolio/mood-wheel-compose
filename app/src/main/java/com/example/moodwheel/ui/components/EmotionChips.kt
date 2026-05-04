package com.example.moodwheel.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.ui.theme.color
import com.example.moodwheel.ui.theme.softColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionChips(
    emotion: MacroEmotion,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        emotion.microEmotions.forEach { label ->
            val isSelected = label in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(label) },
                label = {
                    Text(
                        text = label.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = emotion.color(),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = emotion.softColor()
                )
            )
        }
    }
}
