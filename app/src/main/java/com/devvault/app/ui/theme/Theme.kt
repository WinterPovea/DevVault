package com.devvault.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DevVaultDarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = TextSecondary,
    outline = OutlineDark
)

@Composable
fun DevVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DevVaultDarkColorScheme,
        typography = Typography,
        content = content
    )
}