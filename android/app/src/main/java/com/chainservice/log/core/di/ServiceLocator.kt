package com.chainservice.log.core.di

import android.content.Context
import com.chainservice.log.data.local.GaragePreferences
import com.chainservice.log.data.local.SettingsPreferences
import com.chainservice.log.data.repository.GarageRepositoryImpl
import com.chainservice.log.data.repository.SettingsRepositoryImpl
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.domain.usecase.CalculateWearUseCase
import com.chainservice.log.domain.usecase.FormatDistanceUseCase
import com.chainservice.log.domain.usecase.LogServiceUseCase
import com.chainservice.log.domain.usecase.NextDueComponentUseCase
import com.chainservice.log.domain.usecase.UpdateOdometerUseCase
import com.chainservice.log.presentation.common.UiMapper

object ServiceLocator {

    private var garage: GarageRepository? = null
    private var settings: SettingsRepository? = null
    private var mapper: UiMapper? = null
    private var updateOdometer: UpdateOdometerUseCase? = null
    private var logService: LogServiceUseCase? = null

    fun init(context: Context) {
        if (garage != null) return
        val application = context.applicationContext
        val garageRepository = GarageRepositoryImpl(GaragePreferences(application))
        val calculateWear = CalculateWearUseCase()
        garage = garageRepository
        settings = SettingsRepositoryImpl(SettingsPreferences(application))
        mapper = UiMapper(
            calculateWear = calculateWear,
            nextDue = NextDueComponentUseCase(calculateWear),
            formatDistance = FormatDistanceUseCase()
        )
        updateOdometer = UpdateOdometerUseCase(garageRepository)
        logService = LogServiceUseCase(garageRepository)
    }

    fun factory(
        bikeId: String = "",
        componentId: String = "",
        recordId: String = ""
    ): AppViewModelFactory = AppViewModelFactory(
        garage = required(garage),
        settings = required(settings),
        mapper = required(mapper),
        updateOdometer = required(updateOdometer),
        logService = required(logService),
        bikeId = bikeId,
        componentId = componentId,
        recordId = recordId
    )

    private fun <T : Any> required(value: T?): T = value ?: error("ServiceLocator is not ready")
}
