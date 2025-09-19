package de.christian2003.petrolindex.plugin.presentation.view.settings

import androidx.compose.ui.res.stringArrayResource
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R


/**
 * Displays a dialog through which the user can configure what happens with the data currently stored
 * in the app, once the user restores a backup.
 *
 * @param onDismiss Callback invoked to dismiss the dialog without any action.
 * @param onConfirm Callback invoked once the user selects an option.
 */
@Composable
fun RestoreBackupDialog(
    onDismiss: () -> Unit,
    onConfirm: (RestoreStrategy) -> Unit
) {
    var selectedRestoreStrategy: RestoreStrategy by remember { mutableStateOf(RestoreStrategy.DELETE_EXISTING_DATA) }

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_import),
                contentDescription = ""
            )
        },
        title = {
            Text(stringResource(R.string.restore_title))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.restore_text))
                RestoreStrategy.entries.forEach { restoreStrategy ->
                    RestoreStrategyRadioButton(
                        restoreStrategy = restoreStrategy,
                        selected = restoreStrategy == selectedRestoreStrategy,
                        onClick = {
                            selectedRestoreStrategy = it
                        }
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedRestoreStrategy)
                }
            ) {
                Text(stringResource(R.string.button_import))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}


/**
 * Displays a radio button for a restore strategy.
 *
 * @param restoreStrategy   Restore strategy for which to display the radio button.
 * @param selected          Whether this radio button is selected.
 * @param onClick           Callback invoked once the radio button is clicked.
 */
@Composable
private fun RestoreStrategyRadioButton(
    restoreStrategy: RestoreStrategy,
    selected: Boolean,
    onClick: (RestoreStrategy) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = dimensionResource(R.dimen.padding_vertical))
            .clickable {
                onClick(restoreStrategy)
            }
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onClick(restoreStrategy)
            }
        )
        Column {
            Text(
                text = stringArrayResource(R.array.restore_restoreStrategy_titles)[restoreStrategy.ordinal],
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = stringArrayResource(R.array.restore_restoreStrategy_texts)[restoreStrategy.ordinal],
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
