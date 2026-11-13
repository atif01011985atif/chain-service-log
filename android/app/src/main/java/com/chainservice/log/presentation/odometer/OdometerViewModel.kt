package com.chainservice.log.presentation.odometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.domain.usecase.UpdateOdometerUseCase
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OdometerViewModel(
    garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val updateOdometer: UpdateOdometerUseCase,
    private val mapper: UiMapper,
    private val bikeId: String
) : ViewModel() {

    private val _state = MutableStateFlow(OdometerUiState(bikeId = bikeId))
    val state: StateFlow<OdometerUiState> = _state.asStateFlow()

    private val saved = Channel<Boolean>(Channel.BUFFERED)
    val completionEvents = saved.receiveAsFlow()

    private var previousKm = 0

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                val bike = data.bike(bikeId)
                previousKm = bike?.odometerKm ?: 0
                Triple(bike?.name.orEmpty(), previousKm, settings.unit)
            }.collect { (name, previous, unit) ->
                _state.update { current ->
                    recompute(
                        current.copy(
                            bikeName = name,
                            unit = unit,
                            previousText = mapper.distance(previous, unit)
                        )
                    )
                }
            }
        }
    }

    fun append(digit: String) {
        _state.update { current ->
            val next = (current.input + digit).trimStart('0').take(7)
            recompute(current.copy(input = next, touched = true))
        }
    }

    fun backspace() {
        _state.update { current ->
            recompute(current.copy(input = current.input.dropLast(1), touched = true))
        }
    }

    fun clear() {
        _state.update { current -> recompute(current.copy(input = "", touched = true)) }
    }

    fun save() {
        val current = _state.value
        if (!current.valid) return
        val entered = current.input.toIntOrNull() ?: return
        val kilometres = mapper.toKilometres(entered, current.unit)
        val applied = updateOdometer(bikeId, kilometres)
        viewModelScope.launch { saved.send(applied) }
    }

    private fun recompute(candidate: OdometerUiState): OdometerUiState {
        val entered = candidate.input.toIntOrNull() ?: 0
        val previousDisplay = mapper.toDisplay(previousKm, candidate.unit)
        val delta = entered - previousDisplay
        return candidate.copy(
            displayValue = if (candidate.input.isEmpty()) "0" else mapper.group(entered),
            previousText = mapper.distance(previousKm, candidate.unit),
            deltaText = mapper.group(if (delta > 0) delta else 0),
            valid = candidate.input.isNotEmpty() && delta > 0
        )
    }
}
