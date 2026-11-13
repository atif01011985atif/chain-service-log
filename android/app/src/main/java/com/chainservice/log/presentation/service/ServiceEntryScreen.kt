package com.chainservice.log.presentation.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.ChoicePill
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.PrimaryButton
import com.chainservice.log.core.ui.ScreenHeader
import com.chainservice.log.core.ui.SectionLabel
import com.chainservice.log.core.ui.TabularNumber
import com.chainservice.log.core.ui.fieldColors
import com.chainservice.log.domain.model.ServiceAction

@Composable
fun ServiceEntryScreen(
    bikeId: String,
    componentId: String,
    onBack: () -> Unit,
    onSaved: (String) -> Unit
) {
    val serviceViewModel: ServiceEntryViewModel = viewModel(
        key = "service-" + bikeId + "-" + componentId,
        factory = ServiceLocator.factory(bikeId = bikeId, componentId = componentId)
    )
    val state by serviceViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        serviceViewModel.completionEvents.collect { recordId -> onSaved(recordId) }
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xD91A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = SignalOrange,
        blobAlignment = Alignment.TopStart,
        dotAlpha = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.log_service),
                subtitle = state.bikeName,
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
                    SectionLabel(text = stringResource(R.string.section_component))
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = state.components, key = { it.id }) { component ->
                            MetalPlate(
                                modifier = Modifier.width(150.dp),
                                borderColor = if (component.id == state.selectedComponentId) {
                                    SignalOrange
                                } else {
                                    TextTertiary.copy(alpha = 0.4f)
                                },
                                onClick = { serviceViewModel.selectComponent(component.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp)
                                        .padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(Labels.sprite(component.type)),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = component.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (component.id == state.selectedComponentId) {
                                            SignalOrange
                                        } else {
                                            TextSecondary
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    SectionLabel(text = stringResource(R.string.section_work_done))
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ServiceAction.entries.chunked(2).forEach { group ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                group.forEach { option ->
                                    ChoicePill(
                                        text = stringResource(Labels.action(option)),
                                        selected = option == state.action,
                                        onClick = { serviceViewModel.selectAction(option) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    SectionLabel(text = stringResource(R.string.section_odometer_at_service))
                }
                item {
                    MetalPlate(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TabularNumber(
                                value = state.odometerText,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(Labels.unit(state.unit)),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = state.odometerInput,
                        onValueChange = serviceViewModel::changeOdometer,
                        label = { Text(text = stringResource(R.string.field_odometer_edit)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = serviceViewModel::changeNote,
                        label = { Text(text = stringResource(R.string.section_note)) },
                        placeholder = { Text(text = stringResource(R.string.note_placeholder)) },
                        minLines = 3,
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = state.costInput,
                        onValueChange = serviceViewModel::changeCost,
                        label = { Text(text = stringResource(R.string.cost_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.cost_caption),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.service_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Lime
                )
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryButton(
                    text = stringResource(R.string.save_service),
                    onClick = serviceViewModel::save,
                    enabled = state.canSave,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
