package de.christian2003.petrolindex.view.diagram

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
 * Composable displays a view with a diagram.
 *
 * @param viewModel         View model for the view.
 * @param onNavigateBack    Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagramView(
    viewModel: DiagramViewModel,
    onNavigateBack: () -> Unit
) {
    val locale = LocalConfiguration.current.locales.get(0) ?: LocaleListCompat.getDefault().get(0)!!
    val petrolEntries: List<PetrolEntry> by viewModel.petrolEntries.collectAsState(initial = emptyList())
    viewModel.loadData(petrolEntries, MaterialTheme.colorScheme)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = if (viewModel.diagramInfo != null) { viewModel.diagramInfo!!.title } else { "" },
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
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.settings_content_description_go_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (viewModel.diagramInfo != null) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.space_horizontal),
                            end = dimensionResource(R.dimen.space_horizontal),
                            bottom = dimensionResource(R.dimen.space_vertical)),
                    text = viewModel.diagramInfo!!.unit,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End
                )
                Diagram(
                    items = viewModel.diagramInfo!!.data,
                    color = viewModel.diagramInfo!!.color,
                    title = viewModel.diagramInfo!!.title,
                    indicatorBuilder = { value ->
                        viewModel.diagramInfo!!.formatValue(locale, value)
                    }
                )
            }
            else {
                CircularProgressIndicator()
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
        LineChart(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = dimensionResource(R.dimen.space_horizontal),
                    bottom = dimensionResource(R.dimen.space_vertical)
                ),
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
