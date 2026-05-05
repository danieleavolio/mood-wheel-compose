package com.example.moodwheel.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        emotion.groups.forEach { group ->
            Text(
                text = group.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = emotion.color()
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (listOf(group.label) + group.children).distinct().forEach { label ->
                    val isSelected = label in selected
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggle(label) },
                        label = {
                            Text(
                                text = label,
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
    }
}
