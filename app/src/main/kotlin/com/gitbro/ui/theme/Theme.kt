package com.gitbro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// darkColorScheme maps our colour palette to Material 3's named slots.
// Compose components like Button, Card, TextField automatically pick up
// these colours — we don't have to set them manually on every component.

private val DarkColorScheme = darkColorScheme(
    primary          = AccentBlue,
    onPrimary        = BgDark,
    background       = BgDark,
    onBackground     = TextPrimary,
    surface          = BgSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = BgInput,
    onSurfaceVariant = TextMuted,
    outline          = BorderColor,
    secondary        = AccentGreen,
    onSecondary      = BgDark,
    error            = ForkPink,
    onError          = Color.White,
)

@Composable
fun GitbroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = GitbroTypography,
        content     = content
    )
}