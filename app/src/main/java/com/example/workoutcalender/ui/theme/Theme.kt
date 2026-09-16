package com.example.workoutcalender.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DisplayFont = FontFamily.SansSerif
val BodyFont = FontFamily.SansSerif

/** Fixed accent used to mark trackers that contribute to Home's overview -- constant across all 5 color themes and both light/dark, so it reads consistently as "this is opted into the overview" regardless of the active theme. */
val OverallGoldAccent = Color(0xFFD4AF37)

/** App-specific palette that doesn't map cleanly onto Material's color roles. */
data class ConsistencyColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val textDim: Color,
    val tileEmpty: Color,
    val border: Color,
    val overallAccent: Color,
)

/** One named theme = a light palette + a dark palette. Dark mode toggles within a theme. */
enum class AppColorTheme(val label: String, val light: ConsistencyColors, val dark: ConsistencyColors) {
    GREEN(
        label = "Sage",
        light = ConsistencyColors(
            background = Color(0xFFF6F7F5),
            surface = Color(0xFFFFFFFF),
            text = Color(0xFF1C1F1E),
            textDim = Color(0xFF787F7C),
            tileEmpty = Color(0xFFE7E9E5),
            border = Color(0xFFE2E4E0),
            overallAccent = Color(0xFF3E7B5D),
        ),
        dark = ConsistencyColors(
            background = Color(0xFF14171A),
            surface = Color(0xFF1B1F23),
            text = Color(0xFFEDEFEF),
            textDim = Color(0xFF8B9298),
            tileEmpty = Color(0xFF22262A),
            border = Color(0xFF2A2F34),
            overallAccent = Color(0xFF4E9578),
        ),
    ),
    BLUE(
        label = "Ocean",
        light = ConsistencyColors(
            background = Color(0xFFF3F6F9),
            surface = Color(0xFFFFFFFF),
            text = Color(0xFF181C22),
            textDim = Color(0xFF6E7A88),
            tileEmpty = Color(0xFFE3E9F0),
            border = Color(0xFFDDE4EC),
            overallAccent = Color(0xFF2F6FB0),
        ),
        dark = ConsistencyColors(
            background = Color(0xFF11161C),
            surface = Color(0xFF171D24),
            text = Color(0xFFE9EDF2),
            textDim = Color(0xFF86909C),
            tileEmpty = Color(0xFF1E252D),
            border = Color(0xFF262E37),
            overallAccent = Color(0xFF4A8FD1),
        ),
    ),
    PINK(
        label = "Blossom",
        light = ConsistencyColors(
            background = Color(0xFFFAF4F6),
            surface = Color(0xFFFFFFFF),
            text = Color(0xFF241A1E),
            textDim = Color(0xFF8A737B),
            tileEmpty = Color(0xFFF1E1E7),
            border = Color(0xFFEEDEE4),
            overallAccent = Color(0xFFC24E80),
        ),
        dark = ConsistencyColors(
            background = Color(0xFF1B1215),
            surface = Color(0xFF22171B),
            text = Color(0xFFF3E7EB),
            textDim = Color(0xFF9C838B),
            tileEmpty = Color(0xFF2A1E22),
            border = Color(0xFF32242A),
            overallAccent = Color(0xFFD9689E),
        ),
    ),
    OFF_WHITE(
        label = "Cream",
        light = ConsistencyColors(
            background = Color(0xFFFAF7F0),
            surface = Color(0xFFFFFFFF),
            text = Color(0xFF201D17),
            textDim = Color(0xFF8A8072),
            tileEmpty = Color(0xFFEFE9DC),
            border = Color(0xFFE9E2D2),
            overallAccent = Color(0xFFB08D57),
        ),
        dark = ConsistencyColors(
            background = Color(0xFF19170F),
            surface = Color(0xFF201D15),
            text = Color(0xFFF1ECDF),
            textDim = Color(0xFF9C9382),
            tileEmpty = Color(0xFF29241A),
            border = Color(0xFF322C20),
            overallAccent = Color(0xFFC9A468),
        ),
    ),
    INDIGO(
        label = "Electric Indigo",
        light = ConsistencyColors(
            background = Color(0xFFF5F3FB),
            surface = Color(0xFFFFFFFF),
            text = Color(0xFF1B1730),
            textDim = Color(0xFF7A7495),
            tileEmpty = Color(0xFFE6E1F5),
            border = Color(0xFFE1DBF2),
            overallAccent = Color(0xFF5B3DF0),
        ),
        dark = ConsistencyColors(
            background = Color(0xFF13111F),
            surface = Color(0xFF1A1729),
            text = Color(0xFFEDEAF7),
            textDim = Color(0xFF938DB3),
            tileEmpty = Color(0xFF221E35),
            border = Color(0xFF2A2540),
            overallAccent = Color(0xFF7C5CFF),
        ),
    ),
}

val LocalConsistencyColors = staticCompositionLocalOf { AppColorTheme.GREEN.light }

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold, fontSize = 160.sp, letterSpacing = (-1.5).sp),
    titleMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.sp),
)

@Composable
fun ConsistencyTheme(
    colorTheme: AppColorTheme = AppColorTheme.GREEN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) colorTheme.dark else colorTheme.light

    val materialScheme = if (darkTheme) {
        darkColorScheme(
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.text,
            onSurface = colors.text,
            primary = colors.overallAccent,
        )
    } else {
        lightColorScheme(
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.text,
            onSurface = colors.text,
            primary = colors.overallAccent,
        )
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalConsistencyColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            content = content,
        )
    }
}