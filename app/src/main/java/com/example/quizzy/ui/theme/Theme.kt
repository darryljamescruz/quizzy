package com.example.quizzy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LightSage,
    onPrimary = DarkSage,
    primaryContainer = DarkSage,
    onPrimaryContainer = LightSage,
    secondary = SageGreen,
    onSecondary = Color.White,
    tertiary = LightSage,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onSurface = LightSage
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = Color.White,
    primaryContainer = LightSage,
    onPrimaryContainer = DarkSage,
    secondary = DarkSage,
    onSecondary = Color.White,
    tertiary = SageGreen,
    background = CreamBackground,
    surface = Color.White,
    onSurface = DarkSage,
    surfaceVariant = Color.White,
    onSurfaceVariant = DarkSage
)

@Composable
fun QuizzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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