package com.example.prueba2tfg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.prueba2tfg.R

// Professional blue and white color palette
private val PrimaryBlue = Color(0xFF1976D2)     // Primary blue
private val LightBlue = Color(0xFF64B5F6)       // Lighter blue for secondary elements
private val DarkBlue = Color(0xFF0D47A1)        // Dark blue for emphasis
private val BackgroundLight = Color(0xFFF5F8FF)  // Very light blue-tinted background
private val BackgroundDark = Color(0xFF102040)   // Dark blue background for dark theme
private val TextOnLight = Color(0xFF263238)      // Dark text for light surfaces
private val TextOnDark = Color(0xFFE3F2FD)       // Light blue-white text for dark surfaces

// Light color scheme (default)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = LightBlue,
    tertiary = DarkBlue,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = TextOnLight,
    onBackground = TextOnLight,
    onSurface = TextOnLight
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = PrimaryBlue,
    tertiary = Color(0xFF81D4FA),
    background = BackgroundDark,
    surface = Color(0xFF1A2C4E),
    onPrimary = Color.Black,
    onSecondary = TextOnDark,
    onBackground = TextOnDark,
    onSurface = TextOnDark
)

// Modern sans-serif font for a clean, professional look
val SansSerif = FontFamily(
    Font(R.font.roboto_mono_regular, FontWeight.Normal),
    Font(R.font.roboto_mono_regular, FontWeight.Bold),
    Font(R.font.roboto_mono_regular, FontWeight.Light)
)

// Professional Typography
val EmailAppTypography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun Prueba2tfgTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmailAppTypography,
        content = content
    )
}