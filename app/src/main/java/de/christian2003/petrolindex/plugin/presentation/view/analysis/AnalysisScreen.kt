package de.christian2003.petrolindex.plugin.presentation.view.analysis

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.application.services.CurrencyFormatterService
import de.christian2003.petrolindex.application.services.DateTimeFormatterService
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster
import de.christian2003.petrolindex.domain.analysis.AnalysisResultClusterType
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Value
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.Instant


/**
 * Displays analysis results.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit
) {
    val currencyFormatter = CurrencyFormatterService()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.analysis_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
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
            if (viewModel.analysisResult == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingIndicator()
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AnalysisPeriodSelector(
                        analysisPeriod = viewModel.analysisPeriod,
                        onAnalysisPeriodChange = { period ->
                            if (period != null) {
                                viewModel.startAnalysis(period)
                            }
                            else {
                                viewModel.isDatePickerDialogVisible = true
                            }
                        }
                    )
                    LineDiagram(
                        title = stringResource(R.string.analysis_pricePerLiter),
                        diagram = viewModel.analysisResult!!.pricePerLiterDiagram,
                        precision = viewModel.analysisPrecision,
                        curvedEdges = true,
                        color = MaterialTheme.colorScheme.primary,
                        indicatorBuilder = {
                            currencyFormatter.format(it)
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                    )

                    ClusterOverview(
                        cluster = viewModel.analysisResult!!.volume,
                        onShowMore = {
                            viewModel.clusterToShow = it
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal)
                        )
                    )
                    ClusterOverview(
                        cluster = viewModel.analysisResult!!.totalPrice,
                        onShowMore = {
                            viewModel.clusterToShow = it
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                    )
                    ClusterOverview(
                        cluster = viewModel.analysisResult!!.distanceTraveled,
                        onShowMore = {
                            viewModel.clusterToShow = it
                        },
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.margin_horizontal),
                            end = dimensionResource(R.dimen.margin_horizontal),
                            bottom = dimensionResource(R.dimen.padding_vertical)
                        )
                    )
                }
            }
        }

        val clusterToShow: AnalysisResultCluster? = viewModel.clusterToShow
        if (clusterToShow != null) {
            ClusterSheet(
                cluster = clusterToShow,
                precision = viewModel.analysisPrecision,
                onDismiss = {
                    viewModel.clusterToShow = null
                }
            )
        }

        if (viewModel.isDatePickerDialogVisible) {
            DateRangePickerModal(
                selectedAnalysisPeriod = viewModel.analysisPeriod,
                onPeriodSelected = { period ->
                    viewModel.isDatePickerDialogVisible = false
                    viewModel.startAnalysis(period)
                },
                onDismiss = {
                    viewModel.isDatePickerDialogVisible = false
                }
            )
        }
    }
}


/**
 * Displays an overview for a cluster.
 *
 * @param cluster       Cluster for which to display the overview.
 * @param onShowMore    Callback invoked to show the cluster sheet for this cluster.
 * @param modifier      Modifier.
 */
@Composable
private fun ClusterOverview(
    cluster: AnalysisResultCluster,
    onShowMore: (AnalysisResultCluster) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = CurrencyFormatterService()
    val title: String = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> stringResource(R.string.analysis_clusterVolume_totalSum)
        AnalysisResultClusterType.TOTAL_PRICE -> stringResource(R.string.analysis_clusterTotalPrice_totalSum)
        AnalysisResultClusterType.DISTANCE_TRAVELED -> stringResource(R.string.analysis_clusterDistanceTraveled_totalSum)
    }
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
    val valueTextResourceId: Int = when(cluster.type) {
        AnalysisResultClusterType.VOLUME -> R.string.format_volume
        AnalysisResultClusterType.TOTAL_PRICE -> R.string.format_totalPrice
        AnalysisResultClusterType.DISTANCE_TRAVELED -> R.string.format_distanceTraveled
    }

    Column(
        horizontalAlignment = Alignment.End,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            )
            Value(
                formattedValue = currencyFormatter.format(cluster.totalSum),
                valueTextResourceId = valueTextResourceId,
                textColor = foregroundColor,
                backgroundColor = backgroundColor
            )
        }
        TextButton(
            onClick = {
                onShowMore(cluster)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.button_showMore))
                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(dimensionResource(R.dimen.image_xxs))
                )
            }
        }
    }
}


/**
 * Displays a row with chips through which to select the date range for the analysis.
 *
 * @param analysisPeriod            Analysis period currently selected.
 * @param onAnalysisPeriodChange    Callback invoked once the analysis period is changed. This passes
 *                                  null if a custom range shall be selected. You need to manually
 *                                  allow the user to select a custom range if null is passed!
 * @param modifier                  Modifier.
 */
@Composable
private fun AnalysisPeriodSelector(
    analysisPeriod: AnalysisPeriod,
    onAnalysisPeriodChange: (AnalysisPeriod?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTimeFormatter = DateTimeFormatterService()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        FilterChip(
            selected = analysisPeriod == AnalysisPeriod.CURRENT_YEAR,
            onClick = {
                if (analysisPeriod != AnalysisPeriod.CURRENT_YEAR) {
                    onAnalysisPeriodChange(AnalysisPeriod.CURRENT_YEAR)
                }
            },
            label = {
                Text(stringResource(R.string.analysis_period_currentYear))
            },
            modifier = Modifier.padding(start = dimensionResource(R.dimen.margin_horizontal))
        )
        FilterChip(
            selected = analysisPeriod == AnalysisPeriod.LAST_YEAR,
            onClick = {
                if (analysisPeriod != AnalysisPeriod.LAST_YEAR) {
                    onAnalysisPeriodChange(AnalysisPeriod.LAST_YEAR)
                }
            },
            label = {
                Text(stringResource(R.string.analysis_period_lastYear))
            },
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        )
        FilterChip(
            selected = analysisPeriod != AnalysisPeriod.CURRENT_YEAR && analysisPeriod != AnalysisPeriod.LAST_YEAR,
            onClick = {
                onAnalysisPeriodChange(null)
            },
            label = {
                Text(if (analysisPeriod != AnalysisPeriod.CURRENT_YEAR && analysisPeriod != AnalysisPeriod.LAST_YEAR) {
                    stringResource(R.string.analysis_period_customValue, dateTimeFormatter.format(analysisPeriod.start), dateTimeFormatter.format(analysisPeriod.end))
                } else {
                    stringResource(R.string.analysis_period_custom)
                })
            },
            modifier = Modifier.padding(end = dimensionResource(R.dimen.margin_horizontal))
        )
    }
}


/**
 * Displays a model date picker through which to select a date range for analysis.
 *
 * @param selectedAnalysisPeriod    Time period that is selected currently.
 * @param onPeriodSelected          Callback invoked once a new time period is selected.
 * @param onDismiss                 Callback invoked to close the dialog without selecting a new
 *                                  date range.
 */
@Composable
private fun DateRangePickerModal(
    selectedAnalysisPeriod: AnalysisPeriod,
    onPeriodSelected: (AnalysisPeriod) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDate = selectedAnalysisPeriod.start,
        initialSelectedEndDate = selectedAnalysisPeriod.end
    )

    val dateTimeFormatter = DateTimeFormatterService()
    val dateFormatter: DatePickerFormatter = object: DatePickerFormatter {
        override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
            return ""
        }

        override fun formatDate(dateMillis: Long?, locale: CalendarLocale, forContentDescription: Boolean): String? {
            val date: LocalDate = if (dateMillis != null) {
                LocalDate.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneOffset.UTC)
            } else {
                LocalDate.now()
            }
            return dateTimeFormatter.format(date)
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startDate: LocalDate? = dateRangePickerState.getSelectedStartDate() ?: selectedAnalysisPeriod.start
                    val endDate: LocalDate? = dateRangePickerState.getSelectedEndDate() ?: selectedAnalysisPeriod.end
                    if (startDate != null && endDate != null) {
                        val selectedPeriod = AnalysisPeriod(startDate, endDate)
                        onPeriodSelected(selectedPeriod)
                    }
                }
            ) {
                Text(stringResource(R.string.button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = dateRangePickerState.displayMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding( //Library has incorrect padding. So we need to override here manually!
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = dateFormatter,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp) //Library has incorrect padding. So we need to override here manually!
                )
            }
        )
    }
}
