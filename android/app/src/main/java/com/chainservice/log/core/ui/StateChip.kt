package com.chainservice.log.core.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.SignalOrangePulse
import com.chainservice.log.core.theme.stateBackground
import com.chainservice.log.core.theme.stateColor
import com.chainservice.log.domain.model.WearState

@Composable
fun StateChip(state: WearState, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "chipPulse")
    val pulse by transition.animateColor(
        initialValue = SignalOrange,
        targetValue = SignalOrangePulse,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.DUE_PULSE_MS),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chipColor"
    )
    val tint = if (state == WearState.DUE) pulse else stateColor(state)
    val shape = RoundedCornerShape(4.dp)
    Text(
        text = stringResource(Labels.state(state)),
        style = MaterialTheme.typography.labelSmall,
        color = tint,
        modifier = modifier
            .background(stateBackground(state), shape)
            .border(BorderStroke(1.dp, tint.copy(alpha = 0.6f)), shape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
