package com.chainservice.log.data.local

import android.content.Context
import android.content.SharedPreferences
import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.AppSettings
import com.chainservice.log.domain.model.DistanceUnit

class SettingsPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun read(): AppSettings {
        val unit = preferences.getString(KEY_UNIT, DistanceUnit.KM.name).orEmpty()
        val accent = preferences.getString(KEY_ACCENT, AccentChoice.ORANGE.name).orEmpty()
        return AppSettings(
            unit = DistanceUnit.entries.firstOrNull { it.name == unit } ?: DistanceUnit.KM,
            accent = AccentChoice.entries.firstOrNull { it.name == accent } ?: AccentChoice.ORANGE
        )
    }

    fun write(settings: AppSettings) {
        preferences.edit()
            .putString(KEY_UNIT, settings.unit.name)
            .putString(KEY_ACCENT, settings.accent.name)
            .apply()
    }

    private companion object {
        const val FILE_NAME = "chain_service_settings"
        const val KEY_UNIT = "unit"
        const val KEY_ACCENT = "accent"
    }
}
