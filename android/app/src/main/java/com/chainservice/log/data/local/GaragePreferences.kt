package com.chainservice.log.data.local

import android.content.Context
import android.content.SharedPreferences

class GaragePreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun readSnapshot(): String = preferences.getString(KEY_SNAPSHOT, "").orEmpty()

    fun writeSnapshot(snapshot: String) {
        preferences.edit().putString(KEY_SNAPSHOT, snapshot).putBoolean(KEY_SEEDED, true).apply()
    }

    fun isSeeded(): Boolean = preferences.getBoolean(KEY_SEEDED, false)

    fun clear() {
        preferences.edit().clear().putBoolean(KEY_SEEDED, true).apply()
    }

    private companion object {
        const val FILE_NAME = "chain_service_garage"
        const val KEY_SNAPSHOT = "snapshot"
        const val KEY_SEEDED = "seeded"
    }
}
