package de.christian2003.petrolindex.view.petrol_entries

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.model.utils.LocaleFormatter


/**
 * Composable displays the view which shows the user all petrol entries from the database.
 *
 * @param viewModel             View model for the view.
 * @param onNavigateBack        Callback to navigate back from the view.
 * @param onPetrolEntrySelected Callback to open the view through which to edit a petrol entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetrolEntriesView(
    viewModel: PetrolEntriesViewModel,
    onNavigateBack: () -> Unit,
    onPetrolEntrySelected: (Int) -> Unit
) {
    val petrolEntries by viewModel.petrolEntries.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = stringResource(R.string.petrol_entries_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
            PetrolEntriesList(
                petrolEntries = petrolEntries,
                onDeleteClicked = { petrolEntry ->
                    viewModel.petrolEntryToDelete = petrolEntry
                },
                onPetrolEntrySelected = onPetrolEntrySelected
            )
        }

        //Dialog to delete:
        val petrolEntry = viewModel.petrolEntryToDelete
        if (petrolEntry != null) {
            ConfirmDeleteDialog(
                petrolEntry = petrolEntry,
                onConfirmDelete = { p ->
                    viewModel.delete(p)
                    viewModel.petrolEntryToDelete = null
                },
                onDismiss = {
                    viewModel.petrolEntryToDelete = null
                }
            )
        }
    }
}


/**
 * Composable displays a list of petrol entries.
 *
 * @param petrolEntries         List of petrol entries to display.
 * @param onDeleteClicked       Callback to delete a petrol entry.
 * @param onPetrolEntrySelected Callback to edit a petrol entry.
 */
@Composable
fun PetrolEntriesList(
    petrolEntries: List<PetrolEntry>,
    onDeleteClicked: (PetrolEntry) -> Unit,
    onPetrolEntrySelected: (Int) -> Unit
) {
    LazyColumn {
        items(petrolEntries) { petrolEntry ->
            PetrolEntryRow (
                petrolEntry = petrolEntry,
                onDeleteClicked = onDeleteClicked,
                onPetrolEntrySelected = onPetrolEntrySelected
            )
        }
    }
}


/**
 * Composable displays a single petrol entry from the database.
 *
 * @param petrolEntry           Petrol entry to display.
 * @param onDeleteClicked       Callback to delete the petrol entry displayed.
 * @param onPetrolEntrySelected Callback to edit the petrol entry displayed.
 */
@Composable
fun PetrolEntryRow(
    petrolEntry: PetrolEntry,
    onDeleteClicked: (PetrolEntry) -> Unit,
    onPetrolEntrySelected: (Int) -> Unit
) {
    val formattedDate = LocaleFormatter.epochSecondToFormattedDate(petrolEntry.epochSecond)
    val formattedPrice = LocaleFormatter.centsToFormattedCurrency(petrolEntry.totalPrice)
    val formattedAmount = LocaleFormatter.millilitersToFormattedLiters(petrolEntry.volume)
    val formattedPricePerLiter = LocaleFormatter.centsToFormattedCurrency(petrolEntry.getPricePerLiter())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPetrolEntrySelected(petrolEntry.id)
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
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
                text = stringResource(R.string.petrol_entries_date).replace("{arg}", formattedDate),
                color = MaterialTheme.colorScheme.primary
            )
            if (petrolEntry.description.isNotEmpty()) {
                Text(
                    text = petrolEntry.description
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.petrol_entries_subline).replace("{totalPrice}", formattedPrice).replace("{volume}", formattedAmount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.petrol_entries_price_per_liter).replace("{arg}", formattedPricePerLiter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(
            onClick = {
                onDeleteClicked(petrolEntry)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(R.string.petrol_entries_content_description_delete),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


/**
 * Composable displays a dialog through which the user can confirm the deletion of a petrol entry.
 *
 * @param petrolEntry       Petrol entry to delete.
 * @param onConfirmDelete   Callback to confirm the deletion of the petrol entry.
 * @param onDismiss         Callback to close the dialog without deleting the petrol entry.
 */
@Composable
fun ConfirmDeleteDialog(
    petrolEntry: PetrolEntry,
    onConfirmDelete: (PetrolEntry) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(R.string.petrol_entries_content_description_delete)
            )
        },
        title = {
            Text(text = stringResource(R.string.petrol_entries_delete_title))
        },
        text = {
            Text(text = stringResource(R.string.petrol_entries_delete_info))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete(petrolEntry)
                }
            ) {
                Text(text = stringResource(R.string.petrol_entries_delete_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.petrol_entries_delete_cancel))
            }
        }
    )
}
