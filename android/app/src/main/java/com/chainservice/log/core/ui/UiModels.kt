package com.chainservice.log.core.ui

import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.WearState

data class ComponentUi(
    val id: String,
    val name: String,
    val type: ComponentType,
    val state: WearState,
    val ratio: Float,
    val percent: Int,
    val remainingKm: Int,
    val wornText: String,
    val limitText: String,
    val remainingText: String,
    val unit: DistanceUnit
)

data class BikeUi(
    val id: String,
    val name: String,
    val frame: FrameType,
    val odometerText: String,
    val unit: DistanceUnit,
    val nearestName: String,
    val nearestRemainingText: String,
    val ratio: Float,
    val percent: Int,
    val state: WearState,
    val nearestType: ComponentType,
    val hasComponents: Boolean
)

data class RecordUi(
    val id: String,
    val dateText: String,
    val bikeName: String,
    val componentName: String,
    val componentType: ComponentType,
    val actionLabel: Int,
    val note: String,
    val odometerText: String,
    val unit: DistanceUnit,
    val costText: String,
    val hasCost: Boolean
)
