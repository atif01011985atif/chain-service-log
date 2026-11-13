package com.chainservice.log.presentation.common

import com.chainservice.log.core.ui.BikeUi
import com.chainservice.log.core.ui.ComponentUi
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.RecordUi
import com.chainservice.log.domain.model.Bike
import com.chainservice.log.domain.model.BikeComponent
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.model.GarageData
import com.chainservice.log.domain.model.ServiceRecord
import com.chainservice.log.domain.model.WearState
import com.chainservice.log.domain.usecase.CalculateWearUseCase
import com.chainservice.log.domain.usecase.FormatDistanceUseCase
import com.chainservice.log.domain.usecase.NextDueComponentUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class UiMapper(
    private val calculateWear: CalculateWearUseCase,
    private val nextDue: NextDueComponentUseCase,
    private val formatDistance: FormatDistanceUseCase
) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    fun distance(kilometres: Int, unit: DistanceUnit): String = formatDistance(kilometres, unit)

    fun toDisplay(kilometres: Int, unit: DistanceUnit): Int = formatDistance.convert(kilometres, unit)

    fun toKilometres(value: Int, unit: DistanceUnit): Int = formatDistance.toKilometres(value, unit)

    fun group(value: Int): String = formatDistance.group(value)

    fun component(part: BikeComponent, odometerKm: Int, unit: DistanceUnit): ComponentUi {
        val wear = calculateWear(part, odometerKm)
        return ComponentUi(
            id = part.id,
            name = part.name,
            type = part.type,
            state = wear.state,
            ratio = wear.ratio,
            percent = (wear.ratio * 100f).roundToInt(),
            remainingKm = wear.remainingKm,
            wornText = formatDistance(wear.wornKm, unit),
            limitText = formatDistance(wear.limitKm, unit),
            remainingText = formatDistance(wear.remainingKm, unit),
            unit = unit
        )
    }

    fun bike(bike: Bike, parts: List<BikeComponent>, unit: DistanceUnit): BikeUi {
        val nearest = nextDue(parts, bike.odometerKm)
        val wear = nearest?.let { calculateWear(it, bike.odometerKm) }
        return BikeUi(
            id = bike.id,
            name = bike.name,
            frame = bike.frame,
            odometerText = formatDistance(bike.odometerKm, unit),
            unit = unit,
            nearestName = nearest?.name.orEmpty(),
            nearestRemainingText = formatDistance(wear?.remainingKm ?: 0, unit),
            ratio = wear?.ratio ?: 0f,
            percent = ((wear?.ratio ?: 0f) * 100f).roundToInt(),
            state = wear?.state ?: WearState.OK,
            nearestType = nearest?.type ?: ComponentType.OTHER,
            hasComponents = nearest != null
        )
    }

    fun record(record: ServiceRecord, data: GarageData, unit: DistanceUnit): RecordUi {
        val part = data.component(record.componentId)
        return RecordUi(
            id = record.id,
            dateText = dateFormat.format(Date(record.timestamp)).uppercase(Locale.US),
            bikeName = data.bike(record.bikeId)?.name.orEmpty(),
            componentName = part?.name.orEmpty(),
            componentType = part?.type ?: ComponentType.OTHER,
            actionLabel = Labels.action(record.action),
            note = record.note,
            odometerText = formatDistance(record.odometerKm, unit),
            unit = unit,
            costText = money(record.cost),
            hasCost = record.cost > 0.0
        )
    }

    fun month(timestamp: Long): String =
        monthFormat.format(Date(timestamp)).uppercase(Locale.US)

    fun money(value: Double): String = "%.1f".format(Locale.US, value)

    fun totalSpent(records: List<ServiceRecord>): Double = records.sumOf { it.cost }

    fun dueCount(data: GarageData): Int = data.bikes.sumOf { bike ->
        data.componentsOf(bike.id).count { calculateWear(it, bike.odometerKm).state == WearState.DUE }
    }
}
