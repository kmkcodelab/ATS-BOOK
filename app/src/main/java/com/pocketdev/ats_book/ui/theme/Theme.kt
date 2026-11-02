package com.pocketdev.ats_book.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = CharcoalDarkBackground,
    primaryContainer = IndigoPrimaryDark,
    onPrimaryContainer = SoftGreyTextPrimary,
    background = CharcoalDarkBackground,
    onBackground = SoftGreyTextPrimary,
    surface = CharcoalDarkSurface,
    onSurface = SoftGreyTextPrimary,
    surfaceVariant = CharcoalDarkCard,
    onSurfaceVariant = SoftGreyTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = WhiteSurface,
    primaryContainer = IndigoLight,
    onPrimaryContainer = WhiteSurface,
    background = OffWhiteBackground,
    onBackground = CharcoalTextPrimary,
    surface = WhiteSurface,
    onSurface = CharcoalTextPrimary,
    surfaceVariant = WhiteSurface,
    onSurfaceVariant = CharcoalTextSecondary
)

@Composable
fun ATSBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
