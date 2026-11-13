package com.chainservice.log.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chainservice.log.R
import com.chainservice.log.core.theme.PlateSurface
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.ChoicePill
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.fieldColors
import com.chainservice.log.domain.model.ComponentType

@Composable
fun ComponentEditorDialog(
    unitLabel: String,
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onInstall: (String, ComponentType, Int, Int) -> Unit
) {
    var type by rememberSaveable { mutableStateOf(ComponentType.CHAIN) }
    var name by rememberSaveable { mutableStateOf("") }
    var limit by rememberSaveable { mutableStateOf(ComponentType.CHAIN.defaultLimitKm.toString()) }
    var start by rememberSaveable { mutableStateOf(currentOdometer.toString()) }
    val fallbackName = stringResource(Labels.type(type))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlateSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = {
            Text(
                text = stringResource(R.string.dialog_component_editor),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.field_part_type),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ComponentType.entries.chunked(3).forEach { group ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            group.forEach { option ->
                                ChoicePill(
                                    text = stringResource(Labels.type(option)),
                                    selected = option == type,
                                    onClick = {
                                        type = option
                                        limit = option.defaultLimitKm.toString()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - group.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(24) },
                    label = { Text(text = stringResource(R.string.field_part_name)) },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { value -> limit = value.filter { it.isDigit() }.take(6) },
                    label = { Text(text = stringResource(R.string.field_wear_limit, unitLabel)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = start,
                    onValueChange = { value -> start = value.filter { it.isDigit() }.take(7) },
                    label = {
                        Text(text = stringResource(R.string.field_start_distance, unitLabel))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onInstall(
                        name.ifBlank { fallbackName },
                        type,
                        start.toIntOrNull() ?: 0,
                        limit.toIntOrNull() ?: type.defaultLimitKm
                    )
                }
            ) {
                Text(
                    text = stringResource(R.string.install),
                    color = SignalOrange,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
