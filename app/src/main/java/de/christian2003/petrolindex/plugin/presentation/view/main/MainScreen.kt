package de.christian2003.petrolindex.plugin.presentation.view.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConsumptionListItem
import de.christian2003.petrolindex.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Headline
import okhttp3.internal.http2.Header
import kotlin.uuid.Uuid


/**
 * Composable displays the main view for the app.
 *
 * @param viewModel                 View model for the view.
 * @param onNavigateToConsumptions  Callback to invoke in order to navigate to the list of
 *                                  consumptions.
 * @param onCreateConsumption       Callback to invoke in order to navigate to the view through
 *                                  which to add a new consumption.
 * @param onNavigateToSettings      Callback to invoke in order to navigate to the app settings.
 * @param onNavigateToAnalysis      Callback invoked to navigate to the analysis screen.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToConsumptions: () -> Unit,
    onCreateConsumption: () -> Unit,
    onEditConsumption: (Uuid) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalysis: () -> Unit
) {
    val recentConsumptions: List<Consumption> by viewModel.recentConsumptions.collectAsState(emptyList())
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
                    onCreateConsumption()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
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
            if (recentConsumptions.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    EmptyPlaceholder(
                        title = stringResource(R.string.main_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.main_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_consumptions)
                    )
                }
            }
            else {
                Headline(stringResource(R.string.main_quickActions_title))
                QuickActions(
                    onAnalysisClicked = onNavigateToAnalysis,
                    onConsumptionsClicked = onNavigateToConsumptions,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )

                Headline(stringResource(R.string.main_consumptions_title))
                ConsumptionsList(
                    consumptions = recentConsumptions,
                    onEditConsumption = { consumption ->
                        onEditConsumption(consumption.id)
                    },
                    onDeleteConsumption = { consumption ->
                        viewModel.consumptionToDelete = consumption
                    },
                    onShowAllConsumptions = onNavigateToConsumptions,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.margin_horizontal),
                        end = dimensionResource(R.dimen.margin_horizontal),
                        bottom = dimensionResource(R.dimen.padding_vertical)
                    )
                )
            }
        }
        if (viewModel.consumptionToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.consumptions_deleteText),
                onDismiss = {
                    viewModel.consumptionToDelete = null
                },
                onConfirm = {
                    viewModel.deleteConsumption()
                }
            )
        }
    }
}


/**
 * Displays a row with quick actions.
 *
 * @param onAnalysisClicked		Callback invoked once the button for analysis is clicked.
 * @param onConsumptionsClicked	Callback invoked once the button to show all consumptions is clicked.
 * @param modifier				Modifier.
 */
@Composable
fun QuickActions(
    onAnalysisClicked: () -> Unit,
    onConsumptionsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        QuickActionsButton(
            painter = painterResource(R.drawable.ic_analysis),
            text = stringResource(R.string.main_quickActions_analysis),
            onClick = onAnalysisClicked,
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.margin_horizontal))
                .width(96.dp)
        )
        QuickActionsButton(
            painter = painterResource(R.drawable.ic_data),
            text = stringResource(R.string.main_quickActions_consumptions),
            onClick = onConsumptionsClicked,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                .width(96.dp)
        )
    }
}


/**
 * Displays a quick action button.
 *
 * @param painter	Painter for the button icon.
 * @param text		Text for the button.
 * @param onClick	Callback invoked once the button is clicked.
 * @param modifier	Modifier.
 */
@Composable
fun QuickActionsButton(
    painter: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        FilledIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledIconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.size(dimensionResource(R.dimen.image_l))
        ) {
            Icon(
                painter = painter,
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(R.dimen.image_s))
            )
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.copy(hyphens = Hyphens.Auto),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical) / 2)
        )
    }
}


/**
 * Displays a list of consumptions.
 *
 * @param consumptions          Consumptions to display.
 * @param onEditConsumption     Callback invoked to edit a consumption.
 * @param onDeleteConsumption   Callback invoked to delete a consumption.
 * @param onShowAllConsumptions Callback invoked to show all consumptions.
 * @param modifier              Modifier.
 */
@Composable
private fun ConsumptionsList(
    consumptions: List<Consumption>,
    onEditConsumption: (Consumption) -> Unit,
    onDeleteConsumption: (Consumption) -> Unit,
    onShowAllConsumptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
    ) {
        consumptions.forEach { consumption ->
            ConsumptionListItem(
                consumption = consumption,
                onEdit = onEditConsumption,
                onDelete = onDeleteConsumption
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        TextButton(
            onClick = onShowAllConsumptions
        ) {
            Text(stringResource(R.string.button_showAllConsumptions))
        }
    }
}


/**
 * Composable displays a card at the top of the view if a new app version is available to download.
 *
 * @param onCancelClicked   Callback invoked once the cancel-button is clicked.
 * @param onConfirmClicked  Callback invoked once the confirm-button is clicked.
 */
@Composable
private fun DownloadCard(
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                )
                Text(
                    text = stringResource(R.string.main_download_message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FlowRow(
                modifier = Modifier.align(Alignment.End).padding(top = dimensionResource(R.dimen.padding_vertical)),
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
