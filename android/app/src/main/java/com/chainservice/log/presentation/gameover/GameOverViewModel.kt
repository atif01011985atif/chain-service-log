package com.chainservice.log.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameOverViewModel(
    garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val mapper: UiMapper,
    private val recordId: String
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                val unit = settings.unit
                val record = data.records.firstOrNull { it.id == recordId }
                    ?: data.records.maxByOrNull { it.timestamp }
                val bike = record?.let { data.bike(it.bikeId) }
                val part = record?.let { data.component(it.componentId) }
                GameOverUiState(
                    record = record?.let { mapper.record(it, data, unit) },
                    component = part?.let { mapper.component(it, bike?.odometerKm ?: 0, unit) },
                    bikeId = record?.bikeId.orEmpty(),
                    componentId = record?.componentId.orEmpty(),
                    totalServices = data.records.size,
                    bikeServices = data.records.count { it.bikeId == record?.bikeId },
                    spentText = mapper.money(mapper.totalSpent(data.records)),
                    hasSpent = mapper.totalSpent(data.records) > 0.0,
                    loaded = data.loaded
                )
            }.collect { mapped -> _state.value = mapped }
        }
    }
}
