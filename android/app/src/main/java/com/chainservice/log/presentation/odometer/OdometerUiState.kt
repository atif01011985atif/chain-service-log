package com.chainservice.log.presentation.odometer

import com.chainservice.log.domain.model.DistanceUnit

data class OdometerUiState(
    val bikeId: String = "",
    val bikeName: String = "",
    val input: String = "",
    val displayValue: String = "0",
    val previousText: String = "0",
    val deltaText: String = "0",
    val unit: DistanceUnit = DistanceUnit.KM,
    val valid: Boolean = false,
    val touched: Boolean = false
)
