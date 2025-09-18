package de.christian2003.petrolindex.plugin.presentation.view.consumptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConsumptionListItem
import de.christian2003.petrolindex.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Headline
import kotlin.uuid.Uuid


/**
 * Composable displays a list of all consumptions.
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
                            contentDescription = "",
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
                text = stringResource(R.string.consumptions_deleteText),
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
private fun ConsumptionsList(
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
        val groupedConsumptions = consumptions.groupBy { consumption ->
            consumption.consumptionDate.withDayOfYear(1)
        }
        LazyColumn {
            groupedConsumptions.forEach { (year, yearConsumptions) ->
                item {
                    Column {
                        HorizontalDivider()
                        Headline(
                            title = year.year.toString()
                        )
                    }
                }
                items(yearConsumptions) { consumption ->
                    ConsumptionListItem(
                        consumption = consumption,
                        onDelete = onDeleteConsumption,
                        onEdit = onEditConsumption
                    )
                }
            }
        }
    }
}
