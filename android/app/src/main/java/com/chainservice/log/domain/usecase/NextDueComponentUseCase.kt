package com.chainservice.log.domain.usecase

import com.chainservice.log.domain.model.BikeComponent

class NextDueComponentUseCase(private val calculateWear: CalculateWearUseCase) {

    operator fun invoke(components: List<BikeComponent>, bikeOdometerKm: Int): BikeComponent? =
        components.minByOrNull { calculateWear(it, bikeOdometerKm).remainingKm }
}
