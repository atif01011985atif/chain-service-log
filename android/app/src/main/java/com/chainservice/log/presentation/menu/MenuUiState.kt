package com.chainservice.log.presentation.menu

import com.chainservice.log.core.ui.BikeUi
import com.chainservice.log.domain.model.DistanceUnit

data class MenuUiState(
    val bikes: List<BikeUi> = emptyList(),
    val totalDistanceText: String = "0",
    val bikeCount: Int = 0,
    val dueCount: Int = 0,
    val serviceCount: Int = 0,
    val spentText: String = "0.0",
    val hasSpent: Boolean = false,
    val unit: DistanceUnit = DistanceUnit.KM,
    val loaded: Boolean = false
) {
    val isEmpty: Boolean get() = bikes.isEmpty()
}
