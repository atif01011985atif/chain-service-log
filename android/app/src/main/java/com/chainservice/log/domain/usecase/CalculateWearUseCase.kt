package com.chainservice.log.domain.usecase

import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.domain.model.BikeComponent
import com.chainservice.log.domain.model.Wear
import com.chainservice.log.domain.model.WearState

class CalculateWearUseCase {

    operator fun invoke(component: BikeComponent, bikeOdometerKm: Int): Wear {
        val limit = if (component.limitKm > 0) component.limitKm else 1
        val worn = (bikeOdometerKm - component.startKm).coerceAtLeast(0)
        val ratio = worn.toFloat() / limit.toFloat()
        val state = when {
            ratio >= 1f -> WearState.DUE
            ratio >= GameConfig.WEAR_SOON_RATIO -> WearState.SOON
            else -> WearState.OK
        }
        return Wear(
            wornKm = worn,
            limitKm = limit,
            remainingKm = (limit - worn).coerceAtLeast(0),
            ratio = ratio.coerceIn(0f, 1f),
            state = state
        )
    }
}
