package com.chainservice.log.data.repository

import com.chainservice.log.domain.model.Bike
import com.chainservice.log.domain.model.BikeComponent
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.GarageData
import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.model.ServiceRecord
import com.chainservice.log.domain.model.SparePart

object GarageSnapshotCodec {

    private const val FIELD = "~"
    private const val LINE = "\n"

    fun sanitize(value: String): String =
        value.replace(FIELD, " ").replace(LINE, " ").replace("\r", " ").trim()

    fun encode(data: GarageData): String {
        val builder = StringBuilder()
        data.bikes.forEach { bike ->
            builder.append("B").append(FIELD).append(bike.id).append(FIELD)
                .append(bike.name).append(FIELD).append(bike.frame.name).append(FIELD)
                .append(bike.odometerKm).append(LINE)
        }
        data.components.forEach { part ->
            builder.append("C").append(FIELD).append(part.id).append(FIELD)
                .append(part.bikeId).append(FIELD).append(part.name).append(FIELD)
                .append(part.type.name).append(FIELD).append(part.startKm).append(FIELD)
                .append(part.limitKm).append(FIELD).append(part.retired).append(LINE)
        }
        data.records.forEach { record ->
            builder.append("E").append(FIELD).append(record.id).append(FIELD)
                .append(record.bikeId).append(FIELD).append(record.componentId).append(FIELD)
                .append(record.action.name).append(FIELD).append(record.odometerKm).append(FIELD)
                .append(record.cost).append(FIELD).append(record.timestamp).append(FIELD)
                .append(record.note).append(LINE)
        }
        data.spares.forEach { spare ->
            builder.append("S").append(FIELD).append(spare.id).append(FIELD)
                .append(spare.name).append(FIELD).append(spare.type.name).append(FIELD)
                .append(spare.count).append(LINE)
        }
        return builder.toString()
    }

    fun decode(snapshot: String): GarageData? {
        if (snapshot.isBlank()) return null
        val bikes = mutableListOf<Bike>()
        val components = mutableListOf<BikeComponent>()
        val records = mutableListOf<ServiceRecord>()
        val spares = mutableListOf<SparePart>()
        var recognised = false
        snapshot.lines().forEach { line ->
            val parts = line.split(FIELD)
            when {
                parts.size >= 5 && parts[0] == "B" -> {
                    bikes.add(
                        Bike(
                            id = parts[1],
                            name = parts[2],
                            frame = frameOf(parts[3]),
                            odometerKm = parts[4].toIntOrNull() ?: 0
                        )
                    )
                    recognised = true
                }

                parts.size >= 8 && parts[0] == "C" -> {
                    components.add(
                        BikeComponent(
                            id = parts[1],
                            bikeId = parts[2],
                            name = parts[3],
                            type = typeOf(parts[4]),
                            startKm = parts[5].toIntOrNull() ?: 0,
                            limitKm = parts[6].toIntOrNull() ?: 1,
                            retired = parts[7].toBooleanStrictOrNull() ?: false
                        )
                    )
                    recognised = true
                }

                parts.size >= 9 && parts[0] == "E" -> {
                    records.add(
                        ServiceRecord(
                            id = parts[1],
                            bikeId = parts[2],
                            componentId = parts[3],
                            action = actionOf(parts[4]),
                            odometerKm = parts[5].toIntOrNull() ?: 0,
                            cost = parts[6].toDoubleOrNull() ?: 0.0,
                            timestamp = parts[7].toLongOrNull() ?: 0L,
                            note = parts.drop(8).joinToString(" ")
                        )
                    )
                    recognised = true
                }

                parts.size >= 5 && parts[0] == "S" -> {
                    spares.add(
                        SparePart(
                            id = parts[1],
                            name = parts[2],
                            type = typeOf(parts[3]),
                            count = parts[4].toIntOrNull() ?: 0
                        )
                    )
                    recognised = true
                }
            }
        }
        if (!recognised) return null
        return GarageData(bikes, components, records, spares, true)
    }

    private fun frameOf(raw: String): FrameType =
        FrameType.entries.firstOrNull { it.name == raw } ?: FrameType.CITY

    private fun typeOf(raw: String): ComponentType =
        ComponentType.entries.firstOrNull { it.name == raw } ?: ComponentType.OTHER

    private fun actionOf(raw: String): ServiceAction =
        ServiceAction.entries.firstOrNull { it.name == raw } ?: ServiceAction.INSPECTED
}
