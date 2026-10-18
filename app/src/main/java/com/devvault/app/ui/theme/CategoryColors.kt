package com.devvault.app.ui.theme

import androidx.compose.ui.graphics.Color

val CategoryPalette = listOf(
    0xFF33C8F2, // Primary Cyan
    0xFF2496ED, // Docker Blue
    0xFF7F52FF, // Kotlin Purple
    0xFFF1502F, // Git Orange
    0xFFF7DF1E, // JS Yellow
    0xFF00E676, // Success Green
    0xFFFF5722, // Deep Orange
    0xFFE91E63  // Pink
)

fun resolverColorCategoria(colorLong: Long): Color {
    return try {
        if (colorLong == 0L) {
            Color.Gray
        } else {
            val color = Color(colorLong)
            if (color.alpha == 0f) Color.Gray else color
        }
    } catch (_: Exception) {
        Color.Gray
    }
}
