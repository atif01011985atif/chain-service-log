package com.chainservice.log.domain.model

data class BikeComponent(
    val id: String,
    val bikeId: String,
    val name: String,
    val type: ComponentType,
    val startKm: Int,
    val limitKm: Int,
    val retired: Boolean
)
