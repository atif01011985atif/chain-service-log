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
import com.chainservice.log.domain.model.FrameType

@Composable
fun AddBikeDialog(
    unitLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String, FrameType, Int) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable { mutableStateOf("") }
    var frame by rememberSaveable { mutableStateOf(FrameType.CITY) }
    val fallbackName = stringResource(R.string.default_bike_name)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlateSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = {
            Text(
                text = stringResource(R.string.dialog_add_bike),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(24) },
                    label = { Text(text = stringResource(R.string.field_bike_name)) },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.field_frame),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FrameType.entries.chunked(3).forEach { group ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            group.forEach { option ->
                                ChoicePill(
                                    text = stringResource(Labels.frame(option)),
                                    selected = option == frame,
                                    onClick = { frame = option },
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
                    value = odometer,
                    onValueChange = { value -> odometer = value.filter { it.isDigit() }.take(7) },
                    label = {
                        Text(text = stringResource(R.string.field_start_odometer, unitLabel))
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
                    onConfirm(
                        name.ifBlank { fallbackName },
                        frame,
                        odometer.toIntOrNull() ?: 0
                    )
                }
            ) {
                Text(
                    text = stringResource(R.string.add_bike),
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
