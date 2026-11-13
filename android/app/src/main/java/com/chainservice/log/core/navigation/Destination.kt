package com.chainservice.log.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(val bikeId: String) : Screen

    data class GameOver(val recordId: String) : Screen

    data class Odometer(val bikeId: String) : Screen

    data class LogService(val bikeId: String, val componentId: String) : Screen

    data object History : Screen

    data object Settings : Screen
}
