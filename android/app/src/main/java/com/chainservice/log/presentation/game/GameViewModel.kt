package com.chainservice.log.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameViewModel(
    private val garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val mapper: UiMapper,
    private val bikeId: String
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState(bikeId = bikeId))
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                val unit = settings.unit
                val bike = data.bike(bikeId)
                if (bike == null) {
                    GameUiState(bikeId = bikeId, missing = true, loaded = data.loaded)
                } else {
                    val parts = data.componentsOf(bike.id)
                    val components = parts.map { mapper.component(it, bike.odometerKm, unit) }
                    val records = data.recordsOf(bike.id)
                    val lastOdometer = records.firstOrNull()?.odometerKm ?: bike.odometerKm
                    val nextDue = components.minByOrNull { it.remainingKm }
                    GameUiState(
                        bikeId = bike.id,
                        bikeName = bike.name,
                        frame = bike.frame,
                        odometerText = mapper.distance(bike.odometerKm, unit),
                        odometerKm = bike.odometerKm,
                        unit = unit,
                        components = components.sortedByDescending { it.ratio },
                        recent = records.take(GameConfig.MAX_RECENT_SERVICE)
                            .map { mapper.record(it, data, unit) },
                        sinceLastServiceText = mapper.distance(
                            (bike.odometerKm - lastOdometer).coerceAtLeast(0),
                            unit
                        ),
                        partCount = parts.size,
                        nextDueText = nextDue?.remainingText ?: mapper.distance(0, unit),
                        missing = false,
                        loaded = data.loaded
                    )
                }
            }.collect { mapped -> _state.value = mapped }
        }
    }

    fun installComponent(name: String, type: ComponentType, startValue: Int, limitValue: Int) {
        val unit = _state.value.unit
        garageRepository.installComponent(
            bikeId = bikeId,
            name = name,
            type = type,
            startKm = mapper.toKilometres(startValue, unit),
            limitKm = mapper.toKilometres(limitValue, unit)
        )
    }

    fun retireComponent(componentId: String) {
        garageRepository.retireComponent(componentId)
    }
}
