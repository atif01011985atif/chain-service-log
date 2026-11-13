package com.chainservice.log.data.repository

import com.chainservice.log.data.local.GaragePreferences
import com.chainservice.log.data.sample.SampleData
import com.chainservice.log.domain.model.Bike
import com.chainservice.log.domain.model.BikeComponent
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.GarageData
import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.model.ServiceRecord
import com.chainservice.log.domain.model.SparePart
import com.chainservice.log.domain.repository.GarageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class GarageRepositoryImpl(private val preferences: GaragePreferences) : GarageRepository {

    private val state = MutableStateFlow(load())

    override val data: StateFlow<GarageData> = state.asStateFlow()

    override fun addBike(name: String, frame: FrameType, odometerKm: Int): String {
        val id = newId("bike")
        mutate { current ->
            current.copy(
                bikes = current.bikes + Bike(
                    id = id,
                    name = GarageSnapshotCodec.sanitize(name),
                    frame = frame,
                    odometerKm = odometerKm.coerceAtLeast(0)
                )
            )
        }
        return id
    }

    override fun setOdometer(bikeId: String, odometerKm: Int) {
        mutate { current ->
            current.copy(
                bikes = current.bikes.map { bike ->
                    if (bike.id == bikeId) bike.copy(odometerKm = odometerKm) else bike
                }
            )
        }
    }

    override fun installComponent(
        bikeId: String,
        name: String,
        type: ComponentType,
        startKm: Int,
        limitKm: Int
    ): String {
        val id = newId("part")
        mutate { current ->
            current.copy(
                components = current.components + BikeComponent(
                    id = id,
                    bikeId = bikeId,
                    name = GarageSnapshotCodec.sanitize(name),
                    type = type,
                    startKm = startKm.coerceAtLeast(0),
                    limitKm = limitKm.coerceAtLeast(1),
                    retired = false
                )
            )
        }
        return id
    }

    override fun resetComponentWear(componentId: String, odometerKm: Int) {
        mutate { current ->
            current.copy(
                components = current.components.map { part ->
                    if (part.id == componentId) part.copy(startKm = odometerKm) else part
                }
            )
        }
    }

    override fun retireComponent(componentId: String) {
        mutate { current ->
            current.copy(
                components = current.components.map { part ->
                    if (part.id == componentId) part.copy(retired = true) else part
                }
            )
        }
    }

    override fun addRecord(
        bikeId: String,
        componentId: String,
        action: ServiceAction,
        odometerKm: Int,
        note: String,
        cost: Double
    ): String {
        val id = newId("log")
        mutate { current ->
            current.copy(
                records = current.records + ServiceRecord(
                    id = id,
                    bikeId = bikeId,
                    componentId = componentId,
                    action = action,
                    odometerKm = odometerKm,
                    note = GarageSnapshotCodec.sanitize(note),
                    cost = cost.coerceAtLeast(0.0),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        return id
    }

    override fun addSpare(name: String, type: ComponentType) {
        mutate { current ->
            current.copy(
                spares = current.spares + SparePart(
                    id = newId("spare"),
                    name = GarageSnapshotCodec.sanitize(name),
                    type = type,
                    count = 1
                )
            )
        }
    }

    override fun changeSpareCount(spareId: String, delta: Int) {
        mutate { current ->
            current.copy(
                spares = current.spares.map { spare ->
                    if (spare.id == spareId) {
                        spare.copy(count = (spare.count + delta).coerceIn(0, 99))
                    } else {
                        spare
                    }
                }
            )
        }
    }

    override fun clearAll() {
        preferences.clear()
        state.value = GarageData(loaded = true)
        preferences.writeSnapshot(GarageSnapshotCodec.encode(state.value))
    }

    override fun exportSnapshot(): String = GarageSnapshotCodec.encode(state.value)

    override fun importSnapshot(snapshot: String): Boolean {
        val decoded = GarageSnapshotCodec.decode(snapshot) ?: return false
        state.value = decoded
        preferences.writeSnapshot(GarageSnapshotCodec.encode(decoded))
        return true
    }

    private fun mutate(block: (GarageData) -> GarageData) {
        state.update { current -> block(current) }
        preferences.writeSnapshot(GarageSnapshotCodec.encode(state.value))
    }

    private fun load(): GarageData {
        if (!preferences.isSeeded()) {
            val seeded = SampleData.build()
            preferences.writeSnapshot(GarageSnapshotCodec.encode(seeded))
            return seeded
        }
        return GarageSnapshotCodec.decode(preferences.readSnapshot()) ?: GarageData(loaded = true)
    }

    private fun newId(prefix: String): String =
        prefix + "-" + UUID.randomUUID().toString().take(8)
}
