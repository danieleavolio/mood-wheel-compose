package com.example.moodwheel.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AppIllustration(
    @Suppress("UNUSED_PARAMETER")
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER")
    contentDescription: String? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val outline = MaterialTheme.colorScheme.outlineVariant

    Canvas(
        modifier = modifier
            .height(172.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
    ) {
        val w = size.width
        val h = size.height
        drawCircle(primary.copy(alpha = 0.16f), radius = h * 0.34f, center = Offset(w * 0.32f, h * 0.48f))
        drawCircle(secondary.copy(alpha = 0.18f), radius = h * 0.27f, center = Offset(w * 0.66f, h * 0.38f))
        drawCircle(tertiary.copy(alpha = 0.14f), radius = h * 0.2f, center = Offset(w * 0.58f, h * 0.68f))

        val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
        drawRoundRect(
            color = outline.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.25f, h * 0.28f),
            size = Size(w * 0.5f, h * 0.44f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx()),
            style = stroke
        )
        drawLine(primary.copy(alpha = 0.72f), Offset(w * 0.37f, h * 0.48f), Offset(w * 0.47f, h * 0.58f), stroke.width, cap = StrokeCap.Round)
        drawLine(primary.copy(alpha = 0.72f), Offset(w * 0.47f, h * 0.58f), Offset(w * 0.64f, h * 0.4f), stroke.width, cap = StrokeCap.Round)
        drawArc(
            color = secondary.copy(alpha = 0.76f),
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(w * 0.38f, h * 0.42f),
            size = Size(w * 0.25f, h * 0.26f),
            style = stroke
        )
    }
}
