package de.christian2003.petrolindex.view.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.database.PetrolEntry
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line


/**
 * Composable displays the main view for the app.
 *
 * @param viewModel                     View model for the view.
 * @param onNavigateToPetrolEntries     Callback to invoke in order to navigate to the list of
 *                                      petrol entries.
 * @param onNavigateToAddPetrolEntry    Callback to invoke in order to navigate to the view through
 *                                      which to add a new petrol entry.
 * @param onNavigateToSettings          Callback to invoke in order to navigate to the app settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    viewModel: MainViewModel,
    onNavigateToPetrolEntries: () -> Unit,
    onNavigateToAddPetrolEntry: () -> Unit,
    onNavigateToSettings: () -> Unit
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
                            imageVector = Icons.AutoMirrored.Rounded.List,
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
                            imageVector = Icons.Rounded.Settings,
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
                .padding(horizontal = dimensionResource(R.dimen.space_horizontal))
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
            DiagramPricePerLitre(
                petrolEntries = petrolEntries
            )
            DiagramDistance(
                petrolEntries = petrolEntries
            )
            DiagramCumulatedExpenses(
                petrolEntries = petrolEntries
            )
            DiagramCumulatedVolume(
                petrolEntries = petrolEntries
            )
        }
    }
}


/**
 * Composable displays the diagram displaying the price per litre.
 *
 * @param petrolEntries Petrol entries whose price per litre to display.
 */
@Composable
fun DiagramPricePerLitre(
    petrolEntries: List<PetrolEntry>
) {
    val prices: List<Double> = petrolEntries.asReversed().map { it.getPricePerLiter().toDouble() / 100.0 }
    val indicatorLabel = stringResource(R.string.main_diagram_price_per_liter_indicator)
    val locale = LocalConfiguration.current.locales.get(0) ?: LocaleListCompat.getDefault().get(0)!!
    Diagram(
        color = MaterialTheme.colorScheme.primary,
        items = prices,
        title = stringResource(R.string.main_diagram_price_per_liter_title),
        indicatorBuilder = { indicator ->
            indicatorLabel.replace("{arg}", String.format(locale, "%.2f", indicator))
        }
    )
}


/**
 * Composable displays the diagram displaying the cumulated expenses.
 *
 * @param petrolEntries Petrol entries whose cumulated expenses to display.
 */
@Composable
fun DiagramCumulatedExpenses(
    petrolEntries: List<PetrolEntry>
) {
    var sum: Int = 0
    val prices: List<Double> = petrolEntries.asReversed().map {
        sum += it.totalPrice
        sum.toDouble() / 100.0
    }
    val indicatorLabel = stringResource(R.string.main_diagram_cumulated_expenses_indicator)
    val locale = LocalConfiguration.current.locales.get(0) ?: LocaleListCompat.getDefault().get(0)!!
    Diagram(
        color = MaterialTheme.colorScheme.tertiary,
        items = prices,
        title = stringResource(R.string.main_diagram_cumulated_expenses_title),
        indicatorBuilder = { indicator ->
            indicatorLabel.replace("{arg}", String.format(locale, "%.2f", indicator))
        }
    )
}


/**
 * Composable displays the diagram displaying the cumulated volume.
 *
 * @param petrolEntries Petrol entries whose cumulated volume to display.
 */
@Composable
fun DiagramCumulatedVolume(
    petrolEntries: List<PetrolEntry>
) {
    var sum: Int = 0
    val prices: List<Double> = petrolEntries.asReversed().map {
        sum += it.volume
        sum.toDouble() / 100.0
    }
    val indicatorLabel = stringResource(R.string.main_diagram_cumulated_volume_indicator)
    val locale = LocalConfiguration.current.locales.get(0) ?: LocaleListCompat.getDefault().get(0)!!
    Diagram(
        color = MaterialTheme.colorScheme.tertiary,
        items = prices,
        title = stringResource(R.string.main_diagram_cumulated_volume_title),
        indicatorBuilder = { indicator ->
            indicatorLabel.replace("{arg}", String.format(locale, "%.2f", indicator))
        }
    )
}


/**
 * Composable displays the diagram displaying the distance traveled.
 *
 * @param petrolEntries Petrol entries whose distance traveled to display.
 */
@Composable
fun DiagramDistance(
    petrolEntries: List<PetrolEntry>
) {
    val distances: MutableList<Double> = mutableListOf()
    petrolEntries.asReversed().forEach { petrolEntry ->
        if (petrolEntry.distanceTraveled != null) {
            distances.add(petrolEntry.distanceTraveled.toDouble())
        }
    }
    val indicatorLabel = stringResource(R.string.main_diagram_distance_indicator)
    Diagram(
        color = MaterialTheme.colorScheme.secondary,
        items = distances.toList(),
        title = stringResource(R.string.main_diagram_distance_title),
        indicatorBuilder = { indicator ->
            indicatorLabel.replace("{arg}", indicator.toInt().toString())
        }
    )
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
            .padding(bottom = dimensionResource(R.dimen.space_vertical)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
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
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.space_horizontal_between))
                )
                Text(
                    text = stringResource(R.string.main_download_message),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            FlowRow(
                modifier = Modifier.align(Alignment.End).padding(top = dimensionResource(R.dimen.space_vertical_between)),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancelClicked,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Text(stringResource(R.string.main_download_cancel))
                }
                TextButton(
                    onClick = onConfirmClicked,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Text(stringResource(R.string.main_download_confirm))
                }
            }
        }
    }
}


/**
 * Composable displays a line chart diagram for the items specified. If the passed list of items
 * is has less than 2 items, nothing will be displayed.
 *
 * @param items             List of data points to display within the line.
 * @param color             Color for the diagram.
 * @param title             Title for the diagram.
 * @param indicatorBuilder  Builder to generate the indicator labels on the y-axis.
 */
@Composable
fun Diagram(
    items: List<Double>,
    color: Color,
    title: String,
    indicatorBuilder: (Double) -> String
) {
    if (items.size >= 2) {
        Text(
            text = title,
            color = color
        )
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.diagram_height))
                .padding(bottom = dimensionResource(R.dimen.space_vertical)),
            data = listOf(
                Line(
                    label = title,
                    values = items,
                    color = SolidColor(color),
                    curvedEdges = false,
                    dotProperties = DotProperties(
                        enabled = true,
                        color = SolidColor(color),
                        strokeWidth = 2.dp,
                        radius = 2.dp
                    ),
                    firstGradientFillColor = color.copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    drawStyle = DrawStyle.Stroke(2.dp)
                )
            ),
            labelHelperProperties = LabelHelperProperties(
                enabled = false
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                contentBuilder = indicatorBuilder
            ),
            gridProperties = GridProperties(
                xAxisProperties = GridProperties.AxisProperties(
                    enabled = true,
                    color = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
                ),
                yAxisProperties = GridProperties.AxisProperties(
                    enabled = false
                )
            )
        )
    }
}
