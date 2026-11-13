package com.chainservice.log.core.theme

import androidx.compose.ui.graphics.Color
import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.WearState

const val PRESET_NAME = "DARK_PREMIUM"

val Charcoal = Color(0xFF1A1D21)
val CharcoalDeep = Color(0xFF131619)
val PlateSurface = Color(0xFF22262B)
val PlateElevated = Color(0xFF2A2F35)
val PlateTrack = Color(0xFF33383E)
val PlateBorder = Color(0xFF3A4046)
val PlateHighlight = Color(0xFF4A5159)
val SignalOrange = Color(0xFFF25F29)
val SignalOrangeDark = Color(0xFFD64A1C)
val SignalOrangeLight = Color(0xFFFF9A6A)
val SignalOrangePulse = Color(0xFFFF8A5B)
val Lime = Color(0xFF9BC53D)
val LimeDark = Color(0xFF75A024)
val LimeLight = Color(0xFFC2E86A)
val Amber = Color(0xFFE8B33A)
val DangerRed = Color(0xFFD94F3D)
val TextPrimary = Color(0xFFECEFF1)
val TextSecondary = Color(0xFF9AA4AD)
val TextTertiary = Color(0xFF6B747C)

val ChipOkBackground = Color(0xFF1F2A17)
val ChipSoonBackground = Color(0xFF2A2516)
val ChipDueBackground = Color(0xFF2C1B12)

data class AccentPalette(
    val base: Color,
    val dark: Color,
    val light: Color
)

val OrangeAccentPalette = AccentPalette(SignalOrange, SignalOrangeDark, SignalOrangeLight)
val LimeAccentPalette = AccentPalette(Lime, LimeDark, LimeLight)

fun paletteOf(choice: AccentChoice): AccentPalette = when (choice) {
    AccentChoice.ORANGE -> OrangeAccentPalette
    AccentChoice.LIME -> LimeAccentPalette
}

fun stateColor(state: WearState): Color = when (state) {
    WearState.OK -> Lime
    WearState.SOON -> Amber
    WearState.DUE -> SignalOrange
}

fun stateBackground(state: WearState): Color = when (state) {
    WearState.OK -> ChipOkBackground
    WearState.SOON -> ChipSoonBackground
    WearState.DUE -> ChipDueBackground
}
