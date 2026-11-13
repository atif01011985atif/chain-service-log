package com.chainservice.log.presentation.app

import androidx.lifecycle.ViewModel
import com.chainservice.log.core.navigation.Screen
import com.chainservice.log.domain.model.AppSettings
import com.chainservice.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(settingsRepository: SettingsRepository) : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _forward = MutableStateFlow(true)
    val forward: StateFlow<Boolean> = _forward.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settings

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        _forward.value = true
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replaceWith(target: Screen) {
        _forward.value = true
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun openMenu() {
        backStack.clear()
        _forward.value = false
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _forward.value = false
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun backTo(target: Screen) {
        while (backStack.isNotEmpty() && backStack.last() != target) {
            backStack.removeAt(backStack.lastIndex)
        }
        if (backStack.isNotEmpty()) {
            back()
        } else {
            _forward.value = false
            _screen.value = target
            _canGoBack.value = false
        }
    }
}
