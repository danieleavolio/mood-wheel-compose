package com.example.moodwheel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object MoodColors {
    val Cream = Color(0xFFFFF3E2)
    val WarmSurface = Color(0xFFFFEAD1)
    val SoftSurface = Color(0xFFF3E2CC)
    val Lavender = Color(0xFF9B8AD6)
    val LavenderSoft = Color(0xFFE5DAF2)
    val Sage = Color(0xFFAFC7A2)
    val SageSoft = Color(0xFFDDEBD4)
    val DustyBlue = Color(0xFFA9C6DD)
    val DustyBlueSoft = Color(0xFFDCECF4)
    val Apricot = Color(0xFFF5B98E)
    val ApricotSoft = Color(0xFFF7DDC7)
    val Butter = Color(0xFFF6D98E)
    val Ink = Color(0xFF243044)
    val MutedInk = Color(0xFF6F7685)

    val EmotionHappiness = Color(0xFFAFCF9D)
    val EmotionSadness = Color(0xFFA8C8E4)
    val EmotionAnger = Color(0xFFE7A095)
    val EmotionFear = Color(0xFFB8ADD8)
    val EmotionDisgust = Color(0xFFF2BC8F)
    val EmotionSurprise = Color(0xFFF3D38D)

    val MoodVeryGood = Color(0xFFAFCF9D)
    val MoodGood = Color(0xFFCFE0A5)
    val MoodNeutral = Color(0xFFF3D38D)
    val MoodBad = Color(0xFFF1BE93)
    val MoodVeryBad = Color(0xFFE7A095)

    val DarkBackground = Color(0xFF171822)
    val DarkSurface = Color(0xFF222330)
    val DarkSurfaceVariant = Color(0xFF2C2B36)
    val DarkLavender = Color(0xFFC8BDF2)
    val DarkSage = Color(0xFFC2D7B8)
    val DarkDustyBlue = Color(0xFFBBD3E6)
    val DarkInk = Color(0xFFF7F1E8)
    val DarkMutedInk = Color(0xFFCFC8BE)
}

private val LightColors: ColorScheme = lightColorScheme(
    primary = MoodColors.Lavender,
    onPrimary = Color.White,
    primaryContainer = MoodColors.LavenderSoft,
    onPrimaryContainer = MoodColors.Ink,
    secondary = MoodColors.Sage,
    onSecondary = MoodColors.Ink,
    secondaryContainer = MoodColors.SageSoft,
    onSecondaryContainer = MoodColors.Ink,
    tertiary = MoodColors.DustyBlue,
    tertiaryContainer = MoodColors.DustyBlueSoft,
    background = MoodColors.Cream,
    onBackground = MoodColors.Ink,
    surface = MoodColors.WarmSurface,
    onSurface = MoodColors.Ink,
    surfaceVariant = MoodColors.SoftSurface,
    onSurfaceVariant = MoodColors.MutedInk,
    outline = Color(0xFFCDBDAA),
    outlineVariant = Color(0xFFE1CFBA)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = MoodColors.DarkLavender,
    onPrimary = Color(0xFF30264C),
    primaryContainer = Color(0xFF4E426C),
    onPrimaryContainer = MoodColors.DarkInk,
    secondary = MoodColors.DarkSage,
    onSecondary = Color(0xFF263420),
    secondaryContainer = Color(0xFF3E4B39),
    onSecondaryContainer = MoodColors.DarkInk,
    tertiary = MoodColors.DarkDustyBlue,
    onTertiary = Color(0xFF203241),
    tertiaryContainer = Color(0xFF3B4C5C),
    onTertiaryContainer = MoodColors.DarkInk,
    background = MoodColors.DarkBackground,
    onBackground = MoodColors.DarkInk,
    surface = MoodColors.DarkSurface,
    onSurface = MoodColors.DarkInk,
    surfaceVariant = MoodColors.DarkSurfaceVariant,
    onSurfaceVariant = MoodColors.DarkMutedInk,
    outline = Color(0xFF77706A),
    outlineVariant = Color(0xFF45404A)
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun MoodWheelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
