package com.chainservice.log.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAccentPalette = staticCompositionLocalOf { OrangeAccentPalette }

private val GarageColorScheme = darkColorScheme(
    primary = SignalOrange,
    onPrimary = Charcoal,
    primaryContainer = SignalOrangeDark,
    onPrimaryContainer = TextPrimary,
    secondary = Lime,
    onSecondary = Charcoal,
    secondaryContainer = ChipOkBackground,
    onSecondaryContainer = Lime,
    tertiary = Amber,
    onTertiary = Charcoal,
    background = Charcoal,
    onBackground = TextPrimary,
    surface = PlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = PlateElevated,
    onSurfaceVariant = TextSecondary,
    outline = PlateBorder,
    outlineVariant = PlateTrack,
    error = DangerRed,
    onError = TextPrimary,
    scrim = CharcoalDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GarageColorScheme,
        typography = AppTypography,
        content = content
    )
}
