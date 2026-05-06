package com.example.moodwheel.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.domain.model.MoodLevel
import com.example.moodwheel.ui.theme.color
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CalmBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF2EEFF),
                    Color(0xFFFFF8F0),
                    Color(0xFFF7FBFF)
                )
            )
        ),
        content = content
    )
}

@Composable
fun MoodOrb(
    level: MoodLevel,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    selected: Boolean = false
) {
    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        val radius = min(this.size.width, this.size.height) / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(levelColor(level), radius = radius, center = center)
        drawCircle(Color.White.copy(alpha = 0.22f), radius = radius * 0.72f, center = center.copy(y = center.y - radius * 0.16f))
        drawFace(level, center, radius)
    }
}

@Composable
fun EmotionArtwork(
    emotion: MacroEmotion?,
    modifier: Modifier = Modifier,
    size: Dp = 116.dp,
    animated: Boolean = false
) {
    val base = emotion?.color() ?: MaterialTheme.colorScheme.primary
    val drift = 0f

    Canvas(modifier = modifier.size(size)) {
        val r = min(this.size.width, this.size.height) / 2f
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        repeat(6) { index ->
            val angle = Math.toRadians((index * 60 + drift).toDouble())
            val petalCenter = Offset(
                x = c.x + cos(angle).toFloat() * r * 0.34f,
                y = c.y + sin(angle).toFloat() * r * 0.34f
            )
            drawCircle(
                color = base.copy(alpha = 0.16f + index * 0.018f),
                radius = r * (0.35f - index * 0.015f),
                center = petalCenter
            )
        }
        drawCircle(base.copy(alpha = 0.92f), radius = r * 0.42f, center = c)
        drawCircle(Color.White.copy(alpha = 0.24f), radius = r * 0.28f, center = c.copy(y = c.y - r * 0.1f))
        drawEmotionMark(emotion?.id, c, r)
    }
}

fun levelColor(level: MoodLevel): Color =
    when (level) {
        MoodLevel.VeryGood -> Color(0xFF6CC35B)
        MoodLevel.Good -> Color(0xFFA8D66D)
        MoodLevel.Neutral -> Color(0xFFFFD45A)
        MoodLevel.Bad -> Color(0xFFFFA14D)
        MoodLevel.VeryBad -> Color(0xFFFF7468)
    }

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFace(
    level: MoodLevel,
    center: Offset,
    radius: Float
) {
    val ink = Color(0xFF2D2A35).copy(alpha = 0.78f)
    drawCircle(ink, radius * 0.055f, Offset(center.x - radius * 0.28f, center.y - radius * 0.1f))
    drawCircle(ink, radius * 0.055f, Offset(center.x + radius * 0.28f, center.y - radius * 0.1f))

    val mouth = Path()
    when (level) {
        MoodLevel.VeryGood -> {
            mouth.moveTo(center.x - radius * 0.34f, center.y + radius * 0.14f)
            mouth.cubicTo(center.x - radius * 0.1f, center.y + radius * 0.42f, center.x + radius * 0.1f, center.y + radius * 0.42f, center.x + radius * 0.34f, center.y + radius * 0.14f)
        }
        MoodLevel.Good -> {
            mouth.moveTo(center.x - radius * 0.28f, center.y + radius * 0.12f)
            mouth.cubicTo(center.x - radius * 0.08f, center.y + radius * 0.3f, center.x + radius * 0.08f, center.y + radius * 0.3f, center.x + radius * 0.28f, center.y + radius * 0.12f)
        }
        MoodLevel.Neutral -> {
            drawLine(ink, Offset(center.x - radius * 0.24f, center.y + radius * 0.2f), Offset(center.x + radius * 0.24f, center.y + radius * 0.2f), strokeWidth = radius * 0.055f, cap = StrokeCap.Round)
            return
        }
        MoodLevel.Bad -> {
            mouth.moveTo(center.x - radius * 0.28f, center.y + radius * 0.3f)
            mouth.cubicTo(center.x - radius * 0.08f, center.y + radius * 0.1f, center.x + radius * 0.08f, center.y + radius * 0.1f, center.x + radius * 0.28f, center.y + radius * 0.3f)
        }
        MoodLevel.VeryBad -> {
            mouth.moveTo(center.x - radius * 0.34f, center.y + radius * 0.34f)
            mouth.cubicTo(center.x - radius * 0.1f, center.y + radius * 0.04f, center.x + radius * 0.1f, center.y + radius * 0.04f, center.x + radius * 0.34f, center.y + radius * 0.34f)
        }
    }
    drawPath(mouth, ink, style = Stroke(width = radius * 0.06f, cap = StrokeCap.Round))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEmotionMark(
    id: String?,
    center: Offset,
    radius: Float
) {
    val ink = Color(0xFF2D2A35).copy(alpha = 0.7f)
    when (id) {
        "happiness" -> drawCircle(Color.White.copy(alpha = 0.82f), radius * 0.08f, center.copy(y = center.y - radius * 0.02f))
        "sadness" -> drawOval(ink.copy(alpha = 0.45f), topLeft = Offset(center.x - radius * 0.1f, center.y), size = Size(radius * 0.2f, radius * 0.32f))
        "anger" -> {
            drawLine(ink, Offset(center.x - radius * 0.14f, center.y - radius * 0.1f), Offset(center.x + radius * 0.14f, center.y + radius * 0.1f), radius * 0.05f, cap = StrokeCap.Round)
            drawLine(ink, Offset(center.x + radius * 0.14f, center.y - radius * 0.1f), Offset(center.x - radius * 0.14f, center.y + radius * 0.1f), radius * 0.05f, cap = StrokeCap.Round)
        }
        "fear" -> drawCircle(Color.White.copy(alpha = 0.8f), radius * 0.14f, center)
        "disgust" -> drawArc(ink, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(center.x - radius * 0.18f, center.y - radius * 0.02f), size = Size(radius * 0.36f, radius * 0.22f), style = Stroke(width = radius * 0.045f, cap = StrokeCap.Round))
        "surprise" -> drawCircle(Color.White.copy(alpha = 0.9f), radius * 0.13f, center)
        else -> drawCircle(Color.White.copy(alpha = 0.72f), radius * 0.1f, center)
    }
}
