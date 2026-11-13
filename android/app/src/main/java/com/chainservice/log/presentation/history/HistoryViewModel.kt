package com.chainservice.log.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.repository.GarageRepository
import com.chainservice.log.domain.repository.SettingsRepository
import com.chainservice.log.presentation.common.UiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HistoryViewModel(
    garageRepository: GarageRepository,
    settingsRepository: SettingsRepository,
    private val mapper: UiMapper
) : ViewModel() {

    private val filter = MutableStateFlow(HistoryFilter())

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                garageRepository.data,
                settingsRepository.settings,
                filter
            ) { data, settings, active ->
                val unit = settings.unit
                val matching = data.records
                    .filter { active.bikeId.isEmpty() || it.bikeId == active.bikeId }
                    .filter { record ->
                        val type = data.component(record.componentId)?.type
                        active.type == null || type == active.type
                    }
                    .sortedByDescending { it.timestamp }
                val sections = matching
                    .groupBy { mapper.month(it.timestamp) }
                    .map { (month, records) ->
                        HistorySection(month, records.map { mapper.record(it, data, unit) })
                    }
                HistoryUiState(
                    sections = sections,
                    bikes = data.bikes.map { HistoryBike(it.id, it.name) },
                    filter = active,
                    totalCount = matching.size,
                    loaded = data.loaded
                )
            }.collect { mapped -> _state.value = mapped }
        }
    }

    fun selectBike(bikeId: String) {
        filter.value = filter.value.copy(bikeId = bikeId)
    }

    fun selectType(type: ComponentType?) {
        filter.value = filter.value.copy(type = type)
    }
}
