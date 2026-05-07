package com.example.moodwheel.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MacroEmotion
import com.example.moodwheel.ui.theme.color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun EmotionWheel(
    selected: MacroEmotion?,
    onSelect: (MacroEmotion) -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions = EmotionCatalog.emotions
    val textColor = MaterialTheme.colorScheme.onSurface
    val centerTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val centerOuterColor = MaterialTheme.colorScheme.surface
    val centerInnerColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(maxWidth = 360.dp)
            .aspectRatio(1f)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(emotions) {
                    detectTapGestures { offset ->
                        emotionAt(offset, size.width.toFloat(), emotions)?.let(onSelect)
                    }
                }
        ) {
            val diameter = min(size.width, size.height)
            val radius = diameter / 2f
            val innerRadius = radius * 0.28f
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseStart = -120f
            val sweep = 360f / emotions.size

            emotions.forEachIndexed { index, emotion ->
                val isSelected = selected?.id == emotion.id
                val extra = if (isSelected) 12f else 0f
                val segmentRadius = radius - 10f + extra
                val topLeft = Offset(center.x - segmentRadius, center.y - segmentRadius)
                val segmentSize = Size(segmentRadius * 2f, segmentRadius * 2f)
                drawArc(
                    color = emotion.color(),
                    startAngle = baseStart + index * sweep + 1.2f,
                    sweepAngle = sweep - 2.4f,
                    useCenter = true,
                    topLeft = topLeft,
                    size = segmentSize
                )

                val angle = Math.toRadians((baseStart + index * sweep + sweep / 2f).toDouble())
                val iconCenter = Offset(
                    x = center.x + cos(angle).toFloat() * radius * 0.39f,
                    y = center.y + sin(angle).toFloat() * radius * 0.39f
                )
                drawEmotionGlyph(emotion.id, iconCenter, radius * 0.11f, Color(0xFF2D2A35).copy(alpha = 0.62f))
            }

            drawCircle(centerOuterColor, innerRadius, center)
            drawCircle(centerInnerColor, innerRadius * 0.98f, center)

            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(
                    (textColor.red * 255).toInt(),
                    (textColor.green * 255).toInt(),
                    (textColor.blue * 255).toInt()
                )
                textAlign = Paint.Align.CENTER
                textSize = 30f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            }
            val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(
                    (centerTextColor.red * 255).toInt(),
                    (centerTextColor.green * 255).toInt(),
                    (centerTextColor.blue * 255).toInt()
                )
                textAlign = Paint.Align.CENTER
                textSize = 25f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            }

            drawIntoCanvas { canvas ->
                emotions.forEachIndexed { index, emotion ->
                    val angle = Math.toRadians((baseStart + index * sweep + sweep / 2f).toDouble())
                    val x = center.x + cos(angle).toFloat() * radius * 0.62f
                    val y = center.y + sin(angle).toFloat() * radius * 0.62f
                    canvas.nativeCanvas.drawText(emotion.label, x, y, labelPaint)
                }
                canvas.nativeCanvas.drawText("Tocca una", center.x, center.y - 8f, centerPaint)
                canvas.nativeCanvas.drawText("categoria", center.x, center.y + 24f, centerPaint)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEmotionGlyph(
    id: String,
    center: Offset,
    radius: Float,
    color: Color
) {
    drawCircle(Color.White.copy(alpha = 0.26f), radius * 1.45f, center)
    drawCircle(color, radius * 0.15f, Offset(center.x - radius * 0.42f, center.y - radius * 0.12f))
    drawCircle(color, radius * 0.15f, Offset(center.x + radius * 0.42f, center.y - radius * 0.12f))
    when (id) {
        "happiness" -> drawArc(
            color,
            startAngle = 18f,
            sweepAngle = 144f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.54f, center.y - radius * 0.08f),
            size = Size(radius * 1.08f, radius * 0.72f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = radius * 0.16f, cap = StrokeCap.Round)
        )
        "sadness", "fear" -> drawArc(
            color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.48f, center.y + radius * 0.1f),
            size = Size(radius * 0.96f, radius * 0.68f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = radius * 0.14f, cap = StrokeCap.Round)
        )
        "anger" -> {
            drawLine(color, Offset(center.x - radius * 0.5f, center.y - radius * 0.45f), Offset(center.x - radius * 0.12f, center.y - radius * 0.22f), radius * 0.12f, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x + radius * 0.5f, center.y - radius * 0.45f), Offset(center.x + radius * 0.12f, center.y - radius * 0.22f), radius * 0.12f, cap = StrokeCap.Round)
            drawLine(color, Offset(center.x - radius * 0.34f, center.y + radius * 0.32f), Offset(center.x + radius * 0.34f, center.y + radius * 0.22f), radius * 0.14f, cap = StrokeCap.Round)
        }
        "surprise" -> drawCircle(color, radius * 0.22f, Offset(center.x, center.y + radius * 0.28f))
        else -> drawLine(color, Offset(center.x - radius * 0.34f, center.y + radius * 0.28f), Offset(center.x + radius * 0.34f, center.y + radius * 0.28f), radius * 0.13f, cap = StrokeCap.Round)
    }
}

private fun emotionAt(
    position: Offset,
    width: Float,
    emotions: List<MacroEmotion>
): MacroEmotion? {
    val radius = width / 2f
    val center = Offset(radius, radius)
    val dx = position.x - center.x
    val dy = position.y - center.y
    val distanceSquared = dx * dx + dy * dy
    val innerRadius = radius * 0.28f
    if (distanceSquared < innerRadius * innerRadius || distanceSquared > radius * radius) return null

    val baseStart = -120.0
    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
    val normalized = ((angle - baseStart) % 360 + 360) % 360
    val index = (normalized / (360.0 / emotions.size)).toInt().coerceIn(emotions.indices)
    return emotions[index]
}
