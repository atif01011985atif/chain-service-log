package com.chainservice.log.presentation.odometer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.DangerRed
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.KeypadKey
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.PrimaryButton
import com.chainservice.log.core.ui.ScreenHeader
import com.chainservice.log.core.ui.TabularNumber

@Composable
fun OdometerScreen(
    bikeId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val odometerViewModel: OdometerViewModel = viewModel(
        key = "odometer-" + bikeId,
        factory = ServiceLocator.factory(bikeId = bikeId)
    )
    val state by odometerViewModel.state.collectAsStateWithLifecycle()
    val digits = (0..9).map { stringResource(Labels.digit(it)) }

    LaunchedEffect(Unit) {
        odometerViewModel.completionEvents.collect { applied ->
            if (applied) onSaved()
        }
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xD91A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = SignalOrange,
        blobAlignment = Alignment.BottomEnd,
        dotAlpha = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.odometer_title),
                subtitle = state.bikeName,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                MetalPlate(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        ) {
                            TabularNumber(
                                value = state.displayValue,
                                style = MaterialTheme.typography.displayLarge,
                                color = SignalOrange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Labels.unit(state.unit)),
                                style = MaterialTheme.typography.titleSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                R.string.odometer_previous,
                                state.previousText,
                                stringResource(Labels.unit(state.unit))
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (state.valid) {
                            Text(
                                text = stringResource(
                                    R.string.odometer_delta,
                                    state.deltaText,
                                    stringResource(Labels.unit(state.unit))
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = Lime
                            )
                        } else if (state.touched) {
                            Text(
                                text = stringResource(R.string.odometer_invalid),
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KeypadRow(
                        keys = listOf(digits[1], digits[2], digits[3]),
                        onDigit = odometerViewModel::append,
                        modifier = Modifier.weight(1f)
                    )
                    KeypadRow(
                        keys = listOf(digits[4], digits[5], digits[6]),
                        onDigit = odometerViewModel::append,
                        modifier = Modifier.weight(1f)
                    )
                    KeypadRow(
                        keys = listOf(digits[7], digits[8], digits[9]),
                        onDigit = odometerViewModel::append,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KeypadKey(
                            onClick = odometerViewModel::clear,
                            label = stringResource(R.string.key_clear),
                            contentColor = Lime,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        KeypadKey(
                            onClick = { odometerViewModel.append(digits[0]) },
                            label = digits[0],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        KeypadKey(
                            onClick = odometerViewModel::backspace,
                            icon = Icons.Filled.Clear,
                            iconDescription = stringResource(R.string.cd_backspace),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                PrimaryButton(
                    text = stringResource(R.string.save_odometer),
                    onClick = odometerViewModel::save,
                    enabled = state.valid,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<String>,
    onDigit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        keys.forEach { key ->
            KeypadKey(
                onClick = { onDigit(key) },
                label = key,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}
