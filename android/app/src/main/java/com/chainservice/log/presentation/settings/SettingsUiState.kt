package com.chainservice.log.presentation.settings

import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit

data class SpareUi(
    val id: String,
    val name: String,
    val type: ComponentType,
    val count: Int
)

data class SettingsUiState(
    val unit: DistanceUnit = DistanceUnit.KM,
    val accent: AccentChoice = AccentChoice.ORANGE,
    val spares: List<SpareUi> = emptyList(),
    val snapshot: String = "",
    val restoreFailed: Boolean = false,
    val loaded: Boolean = false
)
