package com.chainservice.log.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.domain.usecase.LogServiceUseCase
import com.chainservice.log.domain.usecase.UpdateOdometerUseCase
import com.chainservice.log.presentation.app.AppViewModel
import com.chainservice.log.presentation.common.UiMapper
import com.chainservice.log.presentation.game.GameViewModel
import com.chainservice.log.presentation.gameover.GameOverViewModel
import com.chainservice.log.presentation.history.HistoryViewModel
import com.chainservice.log.presentation.menu.MenuViewModel
import com.chainservice.log.presentation.odometer.OdometerViewModel
import com.chainservice.log.presentation.service.ServiceEntryViewModel
import com.chainservice.log.presentation.settings.SettingsViewModel
import com.chainservice.log.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val garage: GarageRepository,
    private val settings: SettingsRepository,
    private val mapper: UiMapper,
    private val updateOdometer: UpdateOdometerUseCase,
    private val logService: LogServiceUseCase,
    private val bikeId: String,
    private val componentId: String,
    private val recordId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(settings)

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(garage, settings, mapper)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(garage, settings, mapper, bikeId)

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(garage, settings, mapper, recordId)

            modelClass.isAssignableFrom(OdometerViewModel::class.java) ->
                OdometerViewModel(garage, settings, updateOdometer, mapper, bikeId)

            modelClass.isAssignableFrom(ServiceEntryViewModel::class.java) ->
                ServiceEntryViewModel(garage, settings, logService, mapper, bikeId, componentId)

            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(garage, settings, mapper)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(garage, settings)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
