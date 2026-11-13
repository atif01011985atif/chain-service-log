package com.chainservice.log.domain.model

data class AppSettings(
    val unit: DistanceUnit = DistanceUnit.KM,
    val accent: AccentChoice = AccentChoice.ORANGE
)
