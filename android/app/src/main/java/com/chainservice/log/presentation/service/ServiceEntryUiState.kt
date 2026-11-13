package com.chainservice.log.presentation.service

import com.chainservice.log.core.ui.ComponentUi
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.model.ServiceAction

data class ServiceEntryUiState(
    val bikeId: String = "",
    val bikeName: String = "",
    val components: List<ComponentUi> = emptyList(),
    val selectedComponentId: String = "",
    val action: ServiceAction = ServiceAction.LUBED,
    val odometerInput: String = "",
    val odometerText: String = "0",
    val note: String = "",
    val costInput: String = "",
    val unit: DistanceUnit = DistanceUnit.KM,
    val loaded: Boolean = false
) {
    val canSave: Boolean
        get() = selectedComponentId.isNotEmpty() && odometerInput.toIntOrNull() != null
}
