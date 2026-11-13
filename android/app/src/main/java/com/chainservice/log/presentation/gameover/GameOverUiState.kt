package com.chainservice.log.presentation.gameover

import com.chainservice.log.core.ui.ComponentUi
import com.chainservice.log.core.ui.RecordUi

data class GameOverUiState(
    val record: RecordUi? = null,
    val component: ComponentUi? = null,
    val bikeId: String = "",
    val componentId: String = "",
    val totalServices: Int = 0,
    val bikeServices: Int = 0,
    val spentText: String = "0.0",
    val hasSpent: Boolean = false,
    val loaded: Boolean = false
)
