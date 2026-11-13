package com.chainservice.log.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.chainservice.log.core.config.GameConfig

@Composable
fun RollingNumber(
    value: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val characters = value.toCharArray().toList()
        characters.forEachIndexed { index, character ->
            val fromRight = characters.size - 1 - index
            AnimatedContent(
                targetState = character,
                transitionSpec = {
                    val delay = GameConfig.DIGIT_ROLL_STAGGER_MS * fromRight
                    (slideInVertically(
                        animationSpec = tween(GameConfig.DIGIT_ROLL_MS, delayMillis = delay)
                    ) { full -> full } + fadeIn(
                        animationSpec = tween(GameConfig.DIGIT_ROLL_MS, delayMillis = delay)
                    )) togetherWith (slideOutVertically(
                        animationSpec = tween(GameConfig.DIGIT_ROLL_MS, delayMillis = delay)
                    ) { full -> -full } + fadeOut(
                        animationSpec = tween(GameConfig.DIGIT_ROLL_MS, delayMillis = delay)
                    ))
                },
                label = "digit"
            ) { shown ->
                Text(
                    text = shown.toString(),
                    style = style.copy(fontFeatureSettings = "tnum"),
                    color = color
                )
            }
        }
    }
}
