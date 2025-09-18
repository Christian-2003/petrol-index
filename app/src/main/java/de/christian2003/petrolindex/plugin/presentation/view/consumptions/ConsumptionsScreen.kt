package de.christian2003.petrolindex.plugin.presentation.view.consumptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.petrolindex.plugin.presentation.ui.composables.EmptyPlaceholder
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.uuid.Uuid


/**
 * Composable displays the view which shows the user all petrol entries from the database.
 *
 * @param viewModel         View model for the screen.
 * @param onNavigateUp      Callback to navigate up the navigation stack.
 * @param onEditConsumption Callback to edit a consumption.
 */
@Composable
fun ConsumptionsScreen(
    viewModel: ConsumptionsViewModel,
    onNavigateUp: () -> Unit,
    onEditConsumption: (Uuid) -> Unit
) {
    val consumptions by viewModel.consumptions.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.consumptions_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.petrol_entries_content_description_go_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ConsumptionsList(
                consumptions = consumptions,
                onDeleteConsumption = { consumption ->
                    viewModel.consumptionToDelete = consumption
                },
                onEditConsumption =  { consumption ->
                    onEditConsumption(consumption.id)
                }
            )
        }

        //Dialog to delete:
        val consumptionToDelete: Consumption? = viewModel.consumptionToDelete
        if (consumptionToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.petrol_entries_delete_info),
                onDismiss = {
                    viewModel.consumptionToDelete = null
                },
                onConfirm = {
                    viewModel.delete()
                }
            )
        }
    }
}


/**
 * Composable displays a list of consumptions.
 *
 * @param consumptions          List of consumptions to display.
 * @param onDeleteConsumption   Callback to delete a consumption.
 * @param onEditConsumption     Callback to edit a consumption.
 */
@Composable
fun ConsumptionsList(
    consumptions: List<Consumption>,
    onDeleteConsumption: (Consumption) -> Unit,
    onEditConsumption: (Consumption) -> Unit
) {
    if (consumptions.isEmpty()) {
        EmptyPlaceholder(
            title = stringResource(R.string.consumptions_emptyPlaceholder_title),
            subtitle = stringResource(R.string.consumptions_emptyPlaceholder_subtitle),
            painter = painterResource(R.drawable.el_consumptions),
            modifier = Modifier.fillMaxSize()
        )
    }
    else {
        LazyColumn {
            items(consumptions) { consumption ->
                ConsumptionsListRow (
                    consumption = consumption,
                    onDelete = onDeleteConsumption,
                    onEdit = onEditConsumption
                )
            }
        }
    }
}


/**
 * Composable displays a single consumption.
 *
 * @param consumption   Consumption to display.
 * @param onDelete      Callback to delete the consumption.
 * @param onEdit        Callback to edit the consumption.
 */
@Composable
fun ConsumptionsListRow(
    consumption: Consumption,
    onDelete: (Consumption) -> Unit,
    onEdit: (Consumption) -> Unit
) {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEdit(consumption)
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical_between),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.space_horizontal_between))
        ) {
            Text(
                text = consumption.consumptionDate.format(dateTimeFormatter),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.format_volume, consumption.volume.toDouble() / 1000.0),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.format_totalPrice, consumption.totalPrice.toDouble() / 100.0),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (consumption.distanceTraveled != null) {
                Text(
                    text = stringResource(R.string.format_distanceTraveled, consumption.distanceTraveled!!.toDouble() / 1000.0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(R.string.format_pricePerLiter, consumption.calculatePricePerLiter().toDouble() / 100.0),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (consumption.description.isNotEmpty()) {
                Text(
                    text = consumption.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        IconButton(
            onClick = {
                onDelete(consumption)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.petrol_entries_content_description_delete),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
