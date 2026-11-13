package com.chainservice.log.domain.repository

import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.GarageData
import com.chainservice.log.domain.model.ServiceAction
import kotlinx.coroutines.flow.StateFlow

interface GarageRepository {

    val data: StateFlow<GarageData>

    fun addBike(name: String, frame: FrameType, odometerKm: Int): String

    fun setOdometer(bikeId: String, odometerKm: Int)

    fun installComponent(
        bikeId: String,
        name: String,
        type: ComponentType,
        startKm: Int,
        limitKm: Int
    ): String

    fun resetComponentWear(componentId: String, odometerKm: Int)

    fun retireComponent(componentId: String)

    fun addRecord(
        bikeId: String,
        componentId: String,
        action: ServiceAction,
        odometerKm: Int,
        note: String,
        cost: Double
    ): String

    fun addSpare(name: String, type: ComponentType)

    fun changeSpareCount(spareId: String, delta: Int)

    fun clearAll()

    fun exportSnapshot(): String

    fun importSnapshot(snapshot: String): Boolean
}
