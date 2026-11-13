package com.chainservice.log.domain.repository

import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.AppSettings
import com.chainservice.log.domain.model.DistanceUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<AppSettings>

    fun setUnit(unit: DistanceUnit)

    fun setAccent(accent: AccentChoice)
}
