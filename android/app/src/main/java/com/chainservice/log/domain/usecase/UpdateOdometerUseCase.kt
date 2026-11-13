package com.chainservice.log.domain.usecase

import com.chainservice.log.domain.repository.GarageRepository

class UpdateOdometerUseCase(private val garage: GarageRepository) {

    operator fun invoke(bikeId: String, newOdometerKm: Int): Boolean {
        val bike = garage.data.value.bike(bikeId) ?: return false
        if (newOdometerKm <= bike.odometerKm) return false
        garage.setOdometer(bikeId, newOdometerKm)
        return true
    }
}
