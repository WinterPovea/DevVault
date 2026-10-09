package com.devvault.app.ui.theme

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
    primary = Color(0xFF33C9F0),
    onPrimary = Color(0xFF05070D),
    secondary = PurpleGrey80,
    tertiary = Color(0xFF7FE0C9),
    background = Color(0xFF141A2B),
    onBackground = Color(0xFFF5F7FA),
    surface = Color(0xFF141A2B),
    onSurface = Color(0xFFF5F7FA),
    onSurfaceVariant = Color(0xFF9AA4B8),
    surfaceContainer = Color(0xFF0C0F1A),
    surfaceContainerHighest = Color(0xFF1C2338),
    surfaceContainerLowest = Color(0xFF05070D),
    outlineVariant = Color(0xFF2A3350)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun DevVaultTheme(
    darkTheme: Boolean = true,
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