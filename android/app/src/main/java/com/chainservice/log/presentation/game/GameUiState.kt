package com.chainservice.log.presentation.game

import com.chainservice.log.core.ui.ComponentUi
import com.chainservice.log.core.ui.RecordUi
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.model.FrameType

data class GameUiState(
    val bikeId: String = "",
    val bikeName: String = "",
    val frame: FrameType = FrameType.CITY,
    val odometerText: String = "0",
    val odometerKm: Int = 0,
    val unit: DistanceUnit = DistanceUnit.KM,
    val components: List<ComponentUi> = emptyList(),
    val recent: List<RecordUi> = emptyList(),
    val sinceLastServiceText: String = "0",
    val partCount: Int = 0,
    val nextDueText: String = "0",
    val missing: Boolean = false,
    val loaded: Boolean = false
) {
    val hasComponents: Boolean get() = components.isNotEmpty()
}
