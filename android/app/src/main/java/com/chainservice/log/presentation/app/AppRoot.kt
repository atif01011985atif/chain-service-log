package com.chainservice.log.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.navigation.Screen
import com.chainservice.log.core.theme.Charcoal
import com.chainservice.log.core.theme.LocalAccentPalette
import com.chainservice.log.core.theme.paletteOf
import com.chainservice.log.presentation.game.GameScreen
import com.chainservice.log.presentation.gameover.GameOverScreen
import com.chainservice.log.presentation.history.HistoryScreen
import com.chainservice.log.presentation.menu.MenuScreen
import com.chainservice.log.presentation.odometer.OdometerScreen
import com.chainservice.log.presentation.service.ServiceEntryScreen
import com.chainservice.log.presentation.settings.SettingsScreen
import com.chainservice.log.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val forward by appViewModel.forward.collectAsStateWithLifecycle()
    val settings by appViewModel.settings.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    CompositionLocalProvider(LocalAccentPalette provides paletteOf(settings.accent)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Charcoal)
        ) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    val duration = GameConfig.SCREEN_TRANSITION_MS
                    if (targetState == Screen.Splash || initialState == Screen.Splash) {
                        fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
                    } else {
                        val direction = if (forward) 1 else -1
                        (slideInHorizontally(tween(duration)) { full -> direction * full / 3 } +
                            fadeIn(tween(duration))) togetherWith
                            (slideOutHorizontally(tween(duration)) { full -> -direction * full / 6 } +
                                fadeOut(tween(duration)))
                    }
                },
                label = "rootScreen"
            ) { current ->
                when (current) {
                    Screen.Splash -> SplashScreen(
                        onFinished = { appViewModel.openMenu() }
                    )

                    Screen.Menu -> MenuScreen(
                        onOpenBike = { bikeId -> appViewModel.navigate(Screen.Game(bikeId)) },
                        onOpenHistory = { appViewModel.navigate(Screen.History) },
                        onOpenSettings = { appViewModel.navigate(Screen.Settings) }
                    )

                    is Screen.Game -> GameScreen(
                        bikeId = current.bikeId,
                        onBack = { appViewModel.back() },
                        onUpdateOdometer = {
                            appViewModel.navigate(Screen.Odometer(current.bikeId))
                        },
                        onLogService = { componentId ->
                            appViewModel.navigate(
                                Screen.LogService(current.bikeId, componentId)
                            )
                        }
                    )

                    is Screen.Odometer -> OdometerScreen(
                        bikeId = current.bikeId,
                        onBack = { appViewModel.back() },
                        onSaved = { appViewModel.back() }
                    )

                    is Screen.LogService -> ServiceEntryScreen(
                        bikeId = current.bikeId,
                        componentId = current.componentId,
                        onBack = { appViewModel.back() },
                        onSaved = { recordId ->
                            appViewModel.replaceWith(Screen.GameOver(recordId))
                        }
                    )

                    is Screen.GameOver -> GameOverScreen(
                        recordId = current.recordId,
                        onLogAgain = { bikeId, componentId ->
                            appViewModel.replaceWith(Screen.LogService(bikeId, componentId))
                        },
                        onBackToBike = { bikeId ->
                            appViewModel.backTo(Screen.Game(bikeId))
                        },
                        onMenu = { appViewModel.openMenu() }
                    )

                    Screen.History -> HistoryScreen(
                        onBack = { appViewModel.back() }
                    )

                    Screen.Settings -> SettingsScreen(
                        onBack = { appViewModel.back() }
                    )
                }
            }
        }
    }
}
