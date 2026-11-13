package com.chainservice.log.domain.usecase

import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.repository.GarageRepository

class LogServiceUseCase(private val garage: GarageRepository) {

    operator fun invoke(
        bikeId: String,
        componentId: String,
        action: ServiceAction,
        odometerKm: Int,
        note: String,
        cost: Double
    ): String {
        val bike = garage.data.value.bike(bikeId)
        if (bike != null && odometerKm > bike.odometerKm) {
            garage.setOdometer(bikeId, odometerKm)
        }
        val id = garage.addRecord(bikeId, componentId, action, odometerKm, note, cost)
        if (action.resetsWear) {
            garage.resetComponentWear(componentId, odometerKm)
        }
        if (action == ServiceAction.RETIRED) {
            garage.retireComponent(componentId)
        }
        return id
    }
}
