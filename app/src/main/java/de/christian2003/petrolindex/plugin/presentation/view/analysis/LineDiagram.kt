package de.christian2003.petrolindex.plugin.presentation.view.analysis

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.analysis.AnalysisDiagram
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.plugin.presentation.ui.composables.EmptyPlaceholder
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import java.time.LocalDate


@Composable
fun LineDiagram(
    title: String,
    diagram: AnalysisDiagram,
    precision: AnalysisPrecision,
    curvedEdges: Boolean,
    color: Color,
    indicatorBuilder: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val line = Line(
        label = "",
        values = diagram.values,
        color = SolidColor(color),
        firstGradientFillColor = color.copy(alpha = 0.5f),
        secondGradientFillColor = color.copy(alpha = 0f),
        curvedEdges = curvedEdges,
        dotProperties = DotProperties(
            enabled = true,
            color = SolidColor(MaterialTheme.colorScheme.surface),
            strokeWidth = 2.dp,
            radius = 2.dp,
            strokeColor = SolidColor(color)
        )
    )

    val labels: List<String> = diagram.values.indices.map { i ->
        when (precision) {
            AnalysisPrecision.MONTH -> {
                val date = diagram.start.plusMonths(i.toLong())
                return@map stringArrayResource(R.array.analysis_monthNames)[date.monthValue - 1]
            }
            AnalysisPrecision.QUARTER -> {
                val date: LocalDate = diagram.start.plusMonths(i * 3L)
                val quarter = (date.monthValue - 1) / 3
                return@map stringArrayResource(R.array.analysis_quarterNames)[quarter]
            }
            AnalysisPrecision.YEAR -> {
                val date: LocalDate = diagram.start.plusYears(i.toLong())
                return@map date.year.toString()
            }
        }
    }

    Column(
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
            .padding(bottom = 48.dp) //Always require bottom padding to make space for x-axis labels!
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )
        if (diagram.values.size <= 1) {
            EmptyPlaceholder(
                title = stringResource(R.string.analysis_cluster_emptyPlaceholder_title),
                subtitle = stringResource(R.string.analysis_cluster_emptyPlaceholder_subtitle),
                painter = painterResource(R.drawable.el_analysis)
            )
        }
        else {
            LineChart(
                data = listOf(line),
                minValue = diagram.min,
                gridProperties = GridProperties(
                    xAxisProperties = GridProperties.AxisProperties(
                        enabled = true,
                        color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                        thickness = 1.dp
                    ),
                    yAxisProperties = GridProperties.AxisProperties(
                        enabled = true,
                        color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                        thickness = 1.dp,
                        lineCount = diagram.values.size
                    )
                ),
                labelHelperProperties = LabelHelperProperties(
                    enabled = false
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    labels = labels,
                    rotation = LabelProperties.Rotation(mode = LabelProperties.Rotation.Mode.Force, degree = -45f)
                ),
                popupProperties = PopupProperties(
                    enabled = true,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    contentBuilder = indicatorBuilder
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    contentBuilder = indicatorBuilder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }
    }
}
