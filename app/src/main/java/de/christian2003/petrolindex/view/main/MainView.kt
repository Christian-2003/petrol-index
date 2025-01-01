package de.christian2003.petrolindex.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.LocaleList
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.Line
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.database.PetrolEntry
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import java.util.Locale


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
                .padding(horizontal = dimensionResource(R.dimen.space_horizontal))
        ) {
            Text(
                text = stringResource(R.string.main_diagram_price_per_liter_title),
                color = MaterialTheme.colorScheme.primary
            )
            DiagramPricePerLitre(
                petrolEntries = petrolEntries
            )
            Text(
                text = stringResource(R.string.main_diagram_cumulated_expenses_title),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.space_vertical))
            )
            CumulatedExpenses(
                petrolEntries = petrolEntries
            )
            Text(
                text = stringResource(R.string.main_diagram_cumulated_volume_title),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.space_vertical))
            )
            CumulatedVolume(
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
fun CumulatedExpenses(
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
        color = MaterialTheme.colorScheme.secondary,
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
fun CumulatedVolume(
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
 * Composable displays a line chart diagram for the items specified.
 *
 * @param items List of data points to display within the line.
 * @param color Color for the diagram.
 * @param title Title for the diagram.
 */
@Composable
fun Diagram(
    items: List<Double>,
    color: Color,
    title: String,
    indicatorBuilder: (Double) -> String
) {
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.diagram_height)),
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
