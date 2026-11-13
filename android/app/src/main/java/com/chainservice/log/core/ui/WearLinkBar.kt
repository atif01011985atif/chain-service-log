package com.chainservice.log.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.core.theme.PlateTrack
import com.chainservice.log.core.theme.stateColor
import com.chainservice.log.domain.model.WearState
import kotlin.math.ceil

@Composable
fun WearLinkBar(
    ratio: Float,
    state: WearState,
    description: String,
    modifier: Modifier = Modifier,
    links: Int = GameConfig.LINK_COUNT,
    linkHeight: Dp = 14.dp,
    animateOnAppear: Boolean = true
) {
    val filled = ceil(ratio.coerceIn(0f, 1f) * links).toInt().coerceIn(0, links)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(filled, animateOnAppear) {
        if (animateOnAppear) {
            progress.animateTo(
                targetValue = filled.toFloat(),
                animationSpec = tween(
                    durationMillis = GameConfig.LINK_FILL_MS + GameConfig.LINK_STAGGER_MS * links,
                    easing = LinearEasing
                )
            )
        } else {
            progress.snapTo(filled.toFloat())
        }
    }
    val tint = stateColor(state)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(linkHeight)
            .semantics { stateDescription = description },
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        repeat(links) { index ->
            val fill = (progress.value - index).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = lerpLink(tint, fill),
                        shape = RoundedCornerShape(2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(linkHeight / 2)
                        .background(Color.Black.copy(alpha = 0.35f))
                )
            }
        }
    }
}

private fun lerpLink(tint: Color, fill: Float): Color =
    if (fill <= 0f) PlateTrack else tint.copy(alpha = 0.35f + 0.65f * fill)
