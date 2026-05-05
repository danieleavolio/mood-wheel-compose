package com.example.moodwheel.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CalmCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFCF8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(Color(0xFF6A54F5), Color(0xFF4B35D1)))
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                        )
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1EDFF),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 1.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
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
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        when (label) {
            "home" -> {
                drawLine(color, Offset(w * 0.18f, h * 0.48f), Offset(w * 0.5f, h * 0.2f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.82f, h * 0.48f), Offset(w * 0.5f, h * 0.2f), stroke.width, cap = StrokeCap.Round)
                drawRoundRect(color, topLeft = Offset(w * 0.28f, h * 0.46f), size = Size(w * 0.44f, h * 0.36f), style = stroke)
            }
            "calendar" -> {
                drawRoundRect(color, topLeft = Offset(w * 0.2f, h * 0.24f), size = Size(w * 0.6f, h * 0.58f), style = stroke)
                drawLine(color, Offset(w * 0.2f, h * 0.4f), Offset(w * 0.8f, h * 0.4f), stroke.width, cap = StrokeCap.Round)
                drawCircle(color, radius = 1.8.dp.toPx(), center = Offset(w * 0.38f, h * 0.58f))
                drawCircle(color, radius = 1.8.dp.toPx(), center = Offset(w * 0.6f, h * 0.58f))
            }
            "stats" -> {
                drawLine(color, Offset(w * 0.22f, h * 0.76f), Offset(w * 0.22f, h * 0.5f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.5f, h * 0.76f), Offset(w * 0.5f, h * 0.28f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.78f, h * 0.76f), Offset(w * 0.78f, h * 0.38f), stroke.width, cap = StrokeCap.Round)
            }
            "diary" -> {
                drawRoundRect(color, topLeft = Offset(w * 0.26f, h * 0.18f), size = Size(w * 0.48f, h * 0.66f), style = stroke)
                drawLine(color, Offset(w * 0.36f, h * 0.42f), Offset(w * 0.64f, h * 0.42f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.36f, h * 0.58f), Offset(w * 0.58f, h * 0.58f), stroke.width, cap = StrokeCap.Round)
            }
            else -> {
                drawRoundRect(color, topLeft = Offset(w * 0.22f, h * 0.28f), size = Size(w * 0.56f, h * 0.48f), style = stroke)
                drawLine(color, Offset(w * 0.5f, h * 0.18f), Offset(w * 0.5f, h * 0.58f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.38f, h * 0.46f), Offset(w * 0.5f, h * 0.58f), stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.62f, h * 0.46f), Offset(w * 0.5f, h * 0.58f), stroke.width, cap = StrokeCap.Round)
            }
        }
    }
}
