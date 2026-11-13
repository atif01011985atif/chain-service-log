package com.chainservice.log.presentation.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.PlateBorder
import com.chainservice.log.core.theme.PlateSurface
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.PrimaryButton
import com.chainservice.log.core.ui.SecondaryButton
import com.chainservice.log.core.ui.StatCard
import com.chainservice.log.core.ui.TabularNumber
import com.chainservice.log.core.ui.WearLinkBar

@Composable
fun GameOverScreen(
    recordId: String,
    onLogAgain: (String, String) -> Unit,
    onBackToBike: (String) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = "gameover-" + recordId,
        factory = ServiceLocator.factory(recordId = recordId)
    )
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    val wrench = remember { Animatable(0f) }
    val ring = remember { Animatable(0f) }

    LaunchedEffect(recordId) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        wrench.animateTo(90f, tween(GameConfig.WRENCH_TURN_MS, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(recordId) {
        ring.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xCC14210F),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = Lime,
        blobAlignment = Alignment.TopCenter,
        dotAlpha = 0.4f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer {
                            val scale = 0.8f + 0.45f * ring.value
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - ring.value
                        }
                        .border(BorderStroke(2.dp, Lime), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(PlateSurface, RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, PlateBorder), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_wrench),
                        modifier = Modifier
                            .size(112.dp)
                            .graphicsLayer { rotationZ = wrench.value },
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.result_headline),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )

            Spacer(modifier = Modifier.height(16.dp))

            val record = state.record
            if (record != null) {
                MetalPlate(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.record_headline,
                                record.componentName,
                                stringResource(record.actionLabel)
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.result_at_odometer),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                modifier = Modifier.weight(1f)
                            )
                            TabularNumber(
                                value = record.odometerText,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                            )
                        }
                        if (record.hasCost) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.cost_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                    modifier = Modifier.weight(1f)
                                )
                                TabularNumber(
                                    value = record.costText,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Lime
                                )
                            }
                        }
                        val component = state.component
                        if (component != null) {
                            WearLinkBar(
                                ratio = component.ratio,
                                state = component.state,
                                description = stringResource(
                                    R.string.wear_state_description,
                                    component.percent
                                )
                            )
                        }
                        if (record.note.isNotEmpty()) {
                            Text(
                                text = record.note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.result_total_services),
                    value = state.totalServices.toString(),
                    accent = SignalOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.result_this_bike),
                    value = state.bikeServices.toString(),
                    accent = Lime,
                    modifier = Modifier.weight(1f)
                )
                if (state.hasSpent) {
                    StatCard(
                        label = stringResource(R.string.label_spent),
                        value = state.spentText,
                        accent = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.result_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = stringResource(R.string.log_again),
                onClick = { onLogAgain(state.bikeId, state.componentId) },
                enabled = state.bikeId.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.back_to_bike),
                onClick = { onBackToBike(state.bikeId) },
                accent = Lime,
                enabled = state.bikeId.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
