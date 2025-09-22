package de.christian2003.petrolindex.plugin.presentation.view.analysis

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.application.services.CurrencyFormatterService
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.analysis.AnalysisResultClusterType
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Value


/**
 * Full-screen sheet displays more info about an analysis cluster.
 *
 * @param cluster   Cluster to display.
 * @param precision Analysis precision.
 * @param onDismiss Callback invoked to dismiss the sheet.
 */
@Composable
fun ClusterSheet(
    cluster: AnalysisResultCluster,
    precision: AnalysisPrecision,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val currencyFormatter = CurrencyFormatterService()

    //Colors:
    val foregroundColor = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> MaterialTheme.colorScheme.onPrimaryContainer
        AnalysisResultClusterType.TOTAL_PRICE -> MaterialTheme.colorScheme.onSecondaryContainer
        AnalysisResultClusterType.DISTANCE_TRAVELED -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    val backgroundColor = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> MaterialTheme.colorScheme.primaryContainer
        AnalysisResultClusterType.TOTAL_PRICE -> MaterialTheme.colorScheme.secondaryContainer
        AnalysisResultClusterType.DISTANCE_TRAVELED -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val diagramColor = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> MaterialTheme.colorScheme.primary
        AnalysisResultClusterType.TOTAL_PRICE -> MaterialTheme.colorScheme.secondary
        AnalysisResultClusterType.DISTANCE_TRAVELED -> MaterialTheme.colorScheme.tertiary
    }
    val valueTextResourceId: Int = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> R.string.format_volume
        AnalysisResultClusterType.TOTAL_PRICE -> R.string.format_totalPrice
        AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.format_distanceTraveled
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(when(cluster.type) {
                            AnalysisResultClusterType.VOLUME -> R.string.analysis_clusterVolume_title
                            AnalysisResultClusterType.TOTAL_PRICE -> R.string.analysis_clusterTotalPrice_title
                            AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.analysis_clusterDistanceTraveled_title
                        })
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                    .verticalScroll(rememberScrollState())
            ) {
                LargeValue(
                    title = when(cluster.type) {
                        AnalysisResultClusterType.VOLUME -> stringResource(R.string.analysis_clusterVolume_totalSum)
                        AnalysisResultClusterType.TOTAL_PRICE -> stringResource(R.string.analysis_clusterTotalPrice_totalSum)
                        AnalysisResultClusterType.DISTANCE_TRAVELED -> stringResource(R.string.analysis_clusterDistanceTraveled_totalSum)
                    },
                    valueTextResourceId = valueTextResourceId,
                    formattedValue = currencyFormatter.format(cluster.totalSum),
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.padding_vertical))
                ) {
                    SmallValue(
                        title = when(cluster.type) {
                            AnalysisResultClusterType.VOLUME -> stringResource(R.string.analysis_clusterVolume_totalAverage)
                            AnalysisResultClusterType.TOTAL_PRICE -> stringResource(R.string.analysis_clusterTotalPrice_totalAverage)
                            AnalysisResultClusterType.DISTANCE_TRAVELED -> stringResource(R.string.analysis_clusterDistanceTraveled_totalAverage)
                        },
                        valueTextResourceId = valueTextResourceId,
                        formattedValue = currencyFormatter.format(cluster.totalAverage),
                        foregroundColor = foregroundColor,
                        backgroundColor = backgroundColor,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    )
                    SmallValue(
                        title = stringResource(
                        when(cluster.type) {
                                AnalysisResultClusterType.VOLUME -> R.string.analysis_clusterVolume_precisionAverage
                                AnalysisResultClusterType.TOTAL_PRICE -> R.string.analysis_clusterTotalPrice_precisionAverage
                                AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.analysis_clusterDistanceTraveled_precisionAverage
                            },
                            when(precision) {
                                AnalysisPrecision.MONTH -> stringResource(R.string.analysis_precision_month)
                                AnalysisPrecision.QUARTER -> stringResource(R.string.analysis_precision_quarter)
                                AnalysisPrecision.YEAR -> stringResource(R.string.analysis_precision_year)
                            }
                        ),
                        valueTextResourceId = valueTextResourceId,
                        formattedValue = currencyFormatter.format(cluster.precisionAverage),
                        foregroundColor = foregroundColor,
                        backgroundColor = backgroundColor,
                        modifier = Modifier.weight(0.5f)
                    )
                }
                LineDiagram(
                    title = stringResource(
                        when(cluster.type) {
                            AnalysisResultClusterType.VOLUME -> R.string.analysis_clusterVolume_valuesTitle
                            AnalysisResultClusterType.TOTAL_PRICE -> R.string.analysis_clusterTotalPrice_valuesTitle
                            AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.analysis_clusterDistanceTraveled_valuesTitle
                        },
                        when(precision) {
                            AnalysisPrecision.MONTH -> stringResource(R.string.analysis_precision_month)
                            AnalysisPrecision.QUARTER -> stringResource(R.string.analysis_precision_quarter)
                            AnalysisPrecision.YEAR -> stringResource(R.string.analysis_precision_year)
                        }
                    ),
                    diagram = cluster.sumDiagram,
                    precision = precision,
                    curvedEdges = true,
                    color = diagramColor,
                    indicatorBuilder = {
                        currencyFormatter.format(it)
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
                LineDiagram(
                    title = stringResource(when(cluster.type) {
                        AnalysisResultClusterType.VOLUME -> R.string.analysis_clusterVolume_cumulatedTitle
                        AnalysisResultClusterType.TOTAL_PRICE -> R.string.analysis_clusterTotalPrice_cumulatedTitle
                        AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.analysis_clusterDistanceTraveled_cumulatedTitle
                    }),
                    diagram = cluster.cumulatedDiagram,
                    precision = precision,
                    curvedEdges = false,
                    color = diagramColor,
                    indicatorBuilder = {
                        currencyFormatter.format(it)
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
            }
        }
    }
}


/**
 * Displays the large value.
 *
 * @param title                 Title
 * @param valueTextResourceId   ID of the resource string to use t display the value.
 * @param formattedValue        Formatted value to display.
 * @param foregroundColor       Foreground color.
 * @param backgroundColor       Background color.
 * @param modifier              Modifier.
 */
@Composable
fun LargeValue(
    title: String,
    valueTextResourceId: Int,
    formattedValue: String,
    foregroundColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.padding_horizontal))
        )
        Value(
            formattedValue = formattedValue,
            valueTextResourceId = valueTextResourceId,
            textColor = foregroundColor,
            backgroundColor = backgroundColor
        )
    }
}


/**
 * Displays the small value.
 *
 * @param title                 Title
 * @param valueTextResourceId   ID of the resource string to use t display the value.
 * @param formattedValue        Formatted value to display.
 * @param foregroundColor       Foreground color.
 * @param backgroundColor       Background color.
 * @param modifier              Modifier.
 */
@Composable
fun SmallValue(
    title: String,
    valueTextResourceId: Int,
    formattedValue: String,
    foregroundColor: Color,
    backgroundColor: Color,
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
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )
        Value(
            formattedValue = formattedValue,
            valueTextResourceId = valueTextResourceId,
            textColor = foregroundColor,
            backgroundColor = backgroundColor
        )
    }
}
