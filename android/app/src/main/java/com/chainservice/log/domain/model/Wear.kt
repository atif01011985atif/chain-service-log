package com.chainservice.log.domain.model

data class Wear(
    val wornKm: Int,
    val limitKm: Int,
    val remainingKm: Int,
    val ratio: Float,
    val state: WearState
)
