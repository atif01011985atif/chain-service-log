package com.chainservice.log.domain.model

data class ServiceRecord(
    val id: String,
    val bikeId: String,
    val componentId: String,
    val action: ServiceAction,
    val odometerKm: Int,
    val note: String,
    val cost: Double,
    val timestamp: Long
)
