package com.example.bluetooth_chat.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- Catppuccin Mocha colors ---
val MochaBase = Color(0xFF1E1E2E)
val MochaMantle = Color(0xFF181825)
val MochaCrust = Color(0xFF11111B)
val MochaText = Color(0xFFD9E0EE)
val MochaSubtext1 = Color(0xFFA6ADC8)
val MochaRosewater = Color(0xFFFFCAD4)
val MochaFlamingo = Color(0xFFF2CDCD)
val MochaPink = Color(0xFFF5BDE6)
val MochaMauve = Color(0xFFC6A0F6)
val MochaRed = Color(0xFFF28FAD)
val MochaMaroon = Color(0xFFE8A2AF)
val MochaPeach = Color(0xFFF8BD96)
val MochaYellow = Color(0xFFF8E1A1)
val MochaGreen = Color(0xFFA6D189)
val MochaTeal = Color(0xFF81C8BE)
val MochaSky = Color(0xFF99D1DB)
val MochaSapphire = Color(0xFF85C1DC)
val MochaBlue = Color(0xFF8CAAEE)
val MochaLavender = Color(0xFFB7BDF8)
val MochaText0 = Color(0xFFF5E0DC)
val MochaSubtext0 = Color(0xFFD9E0EE)

// --- Dark color scheme ---
private val DarkColorScheme = darkColorScheme(
    primary = MochaBlue,
    onPrimary = MochaMantle,
    secondary = MochaTeal,
    onSecondary = MochaMantle,
    tertiary = MochaMauve,
    onTertiary = MochaMantle,
    background = MochaBase,
    onBackground = MochaText,
    surface = MochaMantle,
    onSurface = MochaText,
    error = MochaRed,
    onError = MochaMantle
)

// --- Light color scheme (adapted from Mocha but lighter) ---
private val LightColorScheme = lightColorScheme(
    primary = MochaBlue,
    onPrimary = MochaMantle,
    secondary = MochaTeal,
    onSecondary = MochaMantle,
    tertiary = MochaMauve,
    onTertiary = MochaMantle,
    background = MochaCrust,
    onBackground = MochaText,
    surface = MochaMantle,
    onSurface = MochaText,
    error = MochaRed,
    onError = MochaMantle
)

// --- Theme wrapper ---
@Composable
fun Bluetooth_chatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
