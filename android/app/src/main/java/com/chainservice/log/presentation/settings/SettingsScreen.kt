package com.chainservice.log.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.DangerRed
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.ChoicePill
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.ScreenHeader
import com.chainservice.log.core.ui.SecondaryButton
import com.chainservice.log.core.ui.SectionLabel
import com.chainservice.log.core.ui.TabularNumber
import com.chainservice.log.domain.model.AccentChoice
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.presentation.dialog.BackupTextDialog
import com.chainservice.log.presentation.dialog.ConfirmRetireDialog

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    var showExport by rememberSaveable { mutableStateOf(false) }
    var showRestore by rememberSaveable { mutableStateOf(false) }
    var showClear by rememberSaveable { mutableStateOf(false) }
    val sparePlaceholder = stringResource(R.string.default_spare_name)

    if (showExport || showRestore) {
        BackupTextDialog(
            snapshot = state.snapshot,
            restoreMode = showRestore,
            restoreFailed = state.restoreFailed,
            onRestore = { snapshot ->
                settingsViewModel.restore(snapshot)
                showRestore = false
            },
            onDismiss = {
                showExport = false
                showRestore = false
                settingsViewModel.dismissRestoreError()
            }
        )
    }

    if (showClear) {
        ConfirmRetireDialog(
            title = stringResource(R.string.clear_data_title),
            message = stringResource(R.string.clear_data_body),
            confirmText = stringResource(R.string.clear_all),
            onConfirm = {
                showClear = false
                settingsViewModel.clearAll()
            },
            onDismiss = { showClear = false }
        )
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xD91A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = Lime,
        blobAlignment = Alignment.BottomEnd,
        dotAlpha = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.settings_title),
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    SectionLabel(text = stringResource(R.string.section_units))
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChoicePill(
                            text = stringResource(R.string.units_km),
                            selected = state.unit == DistanceUnit.KM,
                            onClick = { settingsViewModel.setUnit(DistanceUnit.KM) },
                            modifier = Modifier.weight(1f)
                        )
                        ChoicePill(
                            text = stringResource(R.string.units_mi),
                            selected = state.unit == DistanceUnit.MI,
                            onClick = { settingsViewModel.setUnit(DistanceUnit.MI) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    SectionLabel(text = stringResource(R.string.section_accent))
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AccentSwatch(
                            label = stringResource(R.string.accent_orange),
                            swatch = SignalOrange,
                            selected = state.accent == AccentChoice.ORANGE,
                            onClick = { settingsViewModel.setAccent(AccentChoice.ORANGE) },
                            modifier = Modifier.weight(1f)
                        )
                        AccentSwatch(
                            label = stringResource(R.string.accent_lime),
                            swatch = Lime,
                            selected = state.accent == AccentChoice.LIME,
                            onClick = { settingsViewModel.setAccent(AccentChoice.LIME) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    SectionLabel(text = stringResource(R.string.section_backup))
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SecondaryButton(
                            text = stringResource(R.string.export_text),
                            onClick = { showExport = true },
                            accent = Lime,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.restore_text),
                            onClick = { showRestore = true },
                            accent = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    SectionLabel(text = stringResource(R.string.section_spares))
                }
                items(items = state.spares, key = { it.id }) { spare ->
                    SpareRow(
                        name = spare.name,
                        count = spare.count,
                        spriteRes = Labels.sprite(spare.type),
                        description = stringResource(
                            R.string.spare_count_description,
                            spare.name,
                            spare.count
                        ),
                        onDecrease = { settingsViewModel.changeSpare(spare.id, -1) },
                        onIncrease = { settingsViewModel.changeSpare(spare.id, 1) }
                    )
                }
                item {
                    SecondaryButton(
                        text = stringResource(R.string.add_spare),
                        onClick = {
                            settingsViewModel.addSpare(
                                sparePlaceholder,
                                ComponentType.OTHER
                            )
                        },
                        accent = Lime,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    SecondaryButton(
                        text = stringResource(R.string.clear_all),
                        onClick = { showClear = true },
                        accent = DangerRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AccentSwatch(
    label: String,
    swatch: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MetalPlate(
        modifier = modifier,
        borderColor = if (selected) swatch else TextTertiary.copy(alpha = 0.4f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(swatch, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) swatch else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SpareRow(
    name: String,
    count: Int,
    spriteRes: Int,
    description: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    MetalPlate(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(horizontal = 12.dp)
                .semantics { stateDescription = description },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(spriteRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDecrease, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = stringResource(R.string.cd_decrease),
                    tint = TextSecondary
                )
            }
            TabularNumber(
                value = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Lime
            )
            IconButton(onClick = onIncrease, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_increase),
                    tint = TextSecondary
                )
            }
        }
    }
}
