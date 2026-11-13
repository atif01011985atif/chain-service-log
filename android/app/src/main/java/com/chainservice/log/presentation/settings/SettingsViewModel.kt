package com.chainservice.log.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val garageRepository: GarageRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garageRepository.data, settingsRepository.settings) { data, settings ->
                SettingsUiState(
                    unit = settings.unit,
                    accent = settings.accent,
                    spares = data.spares.map { SpareUi(it.id, it.name, it.type, it.count) },
                    snapshot = garageRepository.exportSnapshot(),
                    restoreFailed = false,
                    loaded = data.loaded
                )
            }.collect { mapped ->
                _state.update { current -> mapped.copy(restoreFailed = current.restoreFailed) }
            }
        }
    }

    fun setUnit(unit: DistanceUnit) {
        settingsRepository.setUnit(unit)
    }

    fun setAccent(accent: AccentChoice) {
        settingsRepository.setAccent(accent)
    }

    fun changeSpare(spareId: String, delta: Int) {
        garageRepository.changeSpareCount(spareId, delta)
    }

    fun addSpare(name: String, type: ComponentType) {
        if (name.isBlank()) return
        garageRepository.addSpare(name, type)
    }

    fun restore(snapshot: String) {
        val restored = garageRepository.importSnapshot(snapshot)
        _state.update { it.copy(restoreFailed = !restored) }
    }

    fun dismissRestoreError() {
        _state.update { it.copy(restoreFailed = false) }
    }

    fun clearAll() {
        garageRepository.clearAll()
    }
}
