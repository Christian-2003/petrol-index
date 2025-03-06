package de.christian2003.petrolindex.view.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.model.diagram.DiagramInfo
import de.christian2003.petrolindex.model.diagram.DiagramType


/**
 * Composable displays the main view for the app.
 *
 * @param viewModel                     View model for the view.
 * @param onNavigateToPetrolEntries     Callback to invoke in order to navigate to the list of
 *                                      petrol entries.
 * @param onNavigateToAddPetrolEntry    Callback to invoke in order to navigate to the view through
 *                                      which to add a new petrol entry.
 * @param onNavigateToSettings          Callback to invoke in order to navigate to the app settings.
 * @param onNavigateToDiagram           Callback invoked in order to navigate to the page displaying
 *                                      a diagram.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    viewModel: MainViewModel,
    onNavigateToPetrolEntries: () -> Unit,
    onNavigateToAddPetrolEntry: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDiagram: (DiagramInfo) -> Unit
) {
    val petrolEntries by viewModel.petrolEntries.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = stringResource(R.string.main_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToPetrolEntries()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_data),
                            contentDescription = stringResource(R.string.main_content_description_view_database),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            onNavigateToSettings()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.main_content_description_settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigateToAddPetrolEntry()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(R.string.main_content_description_add_petrol_entry)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(viewModel.isUpdateAvailable && !viewModel.isUpdateMessageDismissed) {
                DownloadCard(
                    onCancelClicked = {
                        viewModel.isUpdateMessageDismissed = true
                    },
                    onConfirmClicked = {
                        viewModel.isUpdateMessageDismissed = true
                        viewModel.requestDownload()
                    }
                )
            }
            Data(
                petrolEntries = petrolEntries,
                type = DiagramType.PRICE_PER_LITER,
                onClick = { diagramInfo ->
                    onNavigateToDiagram(diagramInfo)
                }
            )
            Data(
                petrolEntries = petrolEntries,
                type = DiagramType.DISTANCE,
                onClick = { diagramInfo ->
                    onNavigateToDiagram(diagramInfo)
                }
            )
            Data(
                petrolEntries = petrolEntries,
                type = DiagramType.CUMULATED_EXPENSES,
                onClick = { diagramInfo ->
                    onNavigateToDiagram(diagramInfo)
                }
            )
            Data(
                petrolEntries = petrolEntries,
                type = DiagramType.CUMULATED_VOLUME,
                onClick = { diagramInfo ->
                    onNavigateToDiagram(diagramInfo)
                }
            )
        }
    }
}


/**
 * Composable displays a short version of the data for a single diagram.
 *
 * @param petrolEntries Petrol entries of the data to display.
 * @param type          Type of the diagram for which to display the short version of the data.
 * @param onClick       Callback invoked once the data is clicked.
 */
@Composable
fun Data(
    petrolEntries: List<PetrolEntry>,
    type: DiagramType,
    onClick: (DiagramInfo) -> Unit
) {
    val diagramInfo = DiagramInfo.createInstance(petrolEntries, LocalContext.current, MaterialTheme.colorScheme, type)

    val locale = LocalConfiguration.current.locales.get(0) ?: LocaleListCompat.getDefault().get(0)!!
    val totalValueFormatted = diagramInfo.getFormattedTotalValue(locale)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(diagramInfo.data.isNotEmpty()) {
                onClick(diagramInfo)
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical_between),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = diagramInfo.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = diagramInfo.labelIndicator.replace("{arg}", totalValueFormatted),
                color = diagramInfo.color,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/**
 * Composable displays a card at the top of the view if a new app version is available to download.
 *
 * @param onCancelClicked   Callback invoked once the cancel-button is clicked.
 * @param onConfirmClicked  Callback invoked once the confirm-button is clicked.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DownloadCard(
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.space_horizontal),
                end = dimensionResource(R.dimen.space_horizontal),
                bottom = dimensionResource(R.dimen.space_vertical)
            ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_warning),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.space_horizontal_between))
                )
                Text(
                    text = stringResource(R.string.main_download_message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FlowRow(
                modifier = Modifier.align(Alignment.End).padding(top = dimensionResource(R.dimen.space_vertical_between)),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancelClicked,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.main_download_cancel))
                }
                TextButton(
                    onClick = onConfirmClicked,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.main_download_confirm))
                }
            }
        }
    }
}
