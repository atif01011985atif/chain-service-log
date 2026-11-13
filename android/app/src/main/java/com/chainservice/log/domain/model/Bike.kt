package com.chainservice.log.domain.model

data class Bike(
    val id: String,
    val name: String,
    val frame: FrameType,
    val odometerKm: Int
)
