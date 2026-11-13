package com.chainservice.log.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val mapper: UiMapper
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    private val events = Channel<String>(Channel.BUFFERED)
    val openBike = events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                val unit = settings.unit
                MenuUiState(
                    bikes = data.bikes.map { bike ->
                        mapper.bike(bike, data.componentsOf(bike.id), unit)
                    },
                    totalDistanceText = mapper.distance(data.bikes.sumOf { it.odometerKm }, unit),
                    bikeCount = data.bikes.size,
                    dueCount = mapper.dueCount(data),
                    serviceCount = data.records.size,
                    spentText = mapper.money(mapper.totalSpent(data.records)),
                    hasSpent = mapper.totalSpent(data.records) > 0.0,
                    unit = unit,
                    loaded = data.loaded
                )
            }.collect { mapped -> _state.value = mapped }
        }
    }

    fun startLogging() {
        val first = _state.value.bikes.firstOrNull() ?: return
        viewModelScope.launch { events.send(first.id) }
    }

    fun addBike(name: String, frame: FrameType, odometerValue: Int) {
        val kilometres = mapper.toKilometres(odometerValue, _state.value.unit)
        val id = garageRepository.addBike(name, frame, kilometres)
        viewModelScope.launch { events.send(id) }
    }
}
