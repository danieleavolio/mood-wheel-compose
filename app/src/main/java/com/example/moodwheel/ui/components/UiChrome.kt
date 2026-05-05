package com.example.moodwheel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CalmCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = { content() }
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5D4AE3),
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        )
    ) {
        Text(text)
    }
}

@Composable
fun StepProgress(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF5D4AE3),
    inactiveColor: Color = Color(0xFFE9E6F6),
    segmentWidth: Dp = 42.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalSteps) { index ->
            val isActive = index < currentStep
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(segmentWidth)
                    .height(if (isActive) 4.dp else 3.dp)
                    .background(
                        color = if (isActive) activeColor else inactiveColor,
                        shape = RoundedCornerShape(100.dp)
                    )
            )
        }
    }
}

@Composable
fun NavGlyph(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Text(text = label, modifier = modifier, color = color)
}
