package com.chainservice.log.data.repository

import com.chainservice.log.data.local.SettingsPreferences
import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.AppSettings
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(private val preferences: SettingsPreferences) : SettingsRepository {

    private val state = MutableStateFlow(preferences.read())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override fun setUnit(unit: DistanceUnit) {
        state.update { it.copy(unit = unit) }
        preferences.write(state.value)
    }

    override fun setAccent(accent: AccentChoice) {
        state.update { it.copy(accent = accent) }
        preferences.write(state.value)
    }
}
