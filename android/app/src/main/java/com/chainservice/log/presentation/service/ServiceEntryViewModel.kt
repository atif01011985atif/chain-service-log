package com.chainservice.log.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.domain.usecase.LogServiceUseCase
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceEntryViewModel(
    garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val logService: LogServiceUseCase,
    private val mapper: UiMapper,
    private val bikeId: String,
    private val initialComponentId: String
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceEntryUiState(bikeId = bikeId))
    val state: StateFlow<ServiceEntryUiState> = _state.asStateFlow()

    private val saved = Channel<String>(Channel.BUFFERED)
    val completionEvents = saved.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                val unit = settings.unit
                val bike = data.bike(bikeId)
                val parts = data.componentsOf(bikeId)
                    .map { mapper.component(it, bike?.odometerKm ?: 0, unit) }
                Triple(bike?.name.orEmpty(), parts, Pair(unit, bike?.odometerKm ?: 0))
            }.collect { (name, parts, unitAndOdometer) ->
                val (unit, odometerKm) = unitAndOdometer
                _state.update { current ->
                    val selected = when {
                        current.selectedComponentId.isNotEmpty() &&
                            parts.any { it.id == current.selectedComponentId } ->
                            current.selectedComponentId

                        parts.any { it.id == initialComponentId } -> initialComponentId
                        else -> parts.firstOrNull()?.id.orEmpty()
                    }
                    val display = mapper.toDisplay(odometerKm, unit)
                    val input = if (current.odometerInput.isEmpty()) {
                        display.toString()
                    } else {
                        current.odometerInput
                    }
                    current.copy(
                        bikeName = name,
                        components = parts,
                        selectedComponentId = selected,
                        unit = unit,
                        odometerInput = input,
                        odometerText = mapper.group(input.toIntOrNull() ?: display),
                        loaded = true
                    )
                }
            }
        }
    }

    fun selectComponent(componentId: String) {
        _state.update { it.copy(selectedComponentId = componentId) }
    }

    fun selectAction(action: ServiceAction) {
        _state.update { it.copy(action = action) }
    }

    fun changeOdometer(value: String) {
        val digits = value.filter { it.isDigit() }.take(7)
        _state.update {
            it.copy(
                odometerInput = digits,
                odometerText = mapper.group(digits.toIntOrNull() ?: 0)
            )
        }
    }

    fun changeNote(value: String) {
        _state.update { it.copy(note = value.take(120)) }
    }

    fun changeCost(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }.take(8)
        _state.update { it.copy(costInput = filtered) }
    }

    fun save() {
        val current = _state.value
        if (!current.canSave) return
        val entered = current.odometerInput.toIntOrNull() ?: return
        val id = logService(
            bikeId = bikeId,
            componentId = current.selectedComponentId,
            action = current.action,
            odometerKm = mapper.toKilometres(entered, current.unit),
            note = current.note,
            cost = current.costInput.toDoubleOrNull() ?: 0.0
        )
        viewModelScope.launch { saved.send(id) }
    }
}
