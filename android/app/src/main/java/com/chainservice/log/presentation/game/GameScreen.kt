package com.chainservice.log.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.Charcoal
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.PlateBorder
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.ComponentPlate
import com.chainservice.log.core.ui.EmptyState
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.PrimaryButton
import com.chainservice.log.core.ui.RollingNumber
import com.chainservice.log.core.ui.ScreenHeader
import com.chainservice.log.core.ui.SecondaryButton
import com.chainservice.log.core.ui.SectionLabel
import com.chainservice.log.presentation.dialog.ComponentEditorDialog

@Composable
fun GameScreen(
    bikeId: String,
    onBack: () -> Unit,
    onUpdateOdometer: () -> Unit,
    onLogService: (String) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game-" + bikeId,
        factory = ServiceLocator.factory(bikeId = bikeId)
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()
    var showEditor by rememberSaveable { mutableStateOf(false) }

    if (showEditor) {
        ComponentEditorDialog(
            unitLabel = stringResource(Labels.unit(state.unit)),
            currentOdometer = state.odometerKm,
            onDismiss = { showEditor = false },
            onInstall = { name, type, start, limit ->
                showEditor = false
                gameViewModel.installComponent(name, type, start, limit)
            }
        )
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xD91A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = Lime,
        blobAlignment = Alignment.CenterStart,
        dotAlpha = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = state.bikeName,
                subtitle = stringResource(Labels.frame(state.frame)),
                onBack = onBack,
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { showEditor = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.cd_parts_icon),
                            modifier = Modifier.size(22.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(12.dp))

                MetalPlate(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .semantics { liveRegion = LiveRegionMode.Polite },
                            verticalAlignment = Alignment.Bottom
                        ) {
                            RollingNumber(
                                value = state.odometerText,
                                style = MaterialTheme.typography.displayMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Labels.unit(state.unit)),
                                style = MaterialTheme.typography.titleSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        TextButton(
                            onClick = onUpdateOdometer,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.update),
                                style = MaterialTheme.typography.labelLarge,
                                color = SignalOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SectionLabel(text = stringResource(R.string.game_section_components))

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!state.hasComponents) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    EmptyState(
                        spriteRes = R.drawable.sprite_5,
                        title = stringResource(R.string.game_empty_components),
                        actionText = stringResource(R.string.install_part),
                        onAction = { showEditor = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = state.components, key = { it.id }) { component ->
                        ComponentPlate(
                            component = component,
                            onClick = { onLogService(component.id) }
                        )
                    }
                    if (state.recent.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            SectionLabel(text = stringResource(R.string.section_recent_service))
                        }
                        items(items = state.recent, key = { it.id }) { record ->
                            MetalPlate(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp)
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(
                                            Labels.sprite(record.componentType)
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(
                                                R.string.record_headline,
                                                record.componentName,
                                                stringResource(record.actionLabel)
                                            ),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = record.dateText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextTertiary
                                        )
                                    }
                                    if (record.hasCost) {
                                        Text(
                                            text = record.costText,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Lime
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            GameControls(
                state = state,
                onLogService = { onLogService(state.components.firstOrNull()?.id.orEmpty()) },
                onUpdateOdometer = onUpdateOdometer,
                onManageParts = { showEditor = true }
            )
        }
    }
}

@Composable
private fun GameControls(
    state: GameUiState,
    onLogService: () -> Unit,
    onUpdateOdometer: () -> Unit,
    onManageParts: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Charcoal.copy(alpha = 0.96f),
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PlateBorder)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatReadout(
                label = stringResource(R.string.since_last_service),
                value = state.sinceLastServiceText,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(PlateBorder)
            )
            StatReadout(
                label = stringResource(R.string.parts),
                value = state.partCount.toString(),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(PlateBorder)
            )
            StatReadout(
                label = stringResource(R.string.next_due),
                value = state.nextDueText,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryButton(
            text = stringResource(R.string.log_service),
            onClick = onLogService,
            icon = Icons.Filled.Build,
            enabled = state.hasComponents,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryButton(
                text = stringResource(R.string.update_odometer),
                onClick = onUpdateOdometer,
                accent = Lime,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.manage_parts),
                onClick = onManageParts,
                accent = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatReadout(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
