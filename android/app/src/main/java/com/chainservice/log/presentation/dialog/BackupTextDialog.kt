package com.chainservice.log.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chainservice.log.R
import com.chainservice.log.core.theme.DangerRed
import com.chainservice.log.core.theme.PlateSurface
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.fieldColors

@Composable
fun BackupTextDialog(
    snapshot: String,
    restoreMode: Boolean,
    restoreFailed: Boolean,
    onRestore: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var draft by remember { mutableStateOf(if (restoreMode) "" else snapshot) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlateSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = {
            Text(
                text = stringResource(R.string.backup_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (restoreMode) {
                        stringResource(R.string.restore_hint)
                    } else {
                        stringResource(R.string.backup_hint)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                if (restoreMode) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        colors = fieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 240.dp)
                    )
                    if (restoreFailed) {
                        Text(
                            text = stringResource(R.string.restore_failed),
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed
                        )
                    }
                } else {
                    SelectionContainer {
                        Text(
                            text = snapshot,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (restoreMode) {
                TextButton(onClick = { onRestore(draft) }) {
                    Text(
                        text = stringResource(R.string.restore),
                        color = SignalOrange,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.close),
                        color = SignalOrange,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        dismissButton = {
            if (restoreMode) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = TextTertiary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}
