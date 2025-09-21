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
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.analysis.AnalysisDiagram
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line


@Composable
fun LineDiagram(
    title: String,
    diagram: AnalysisDiagram,
    curvedEdges: Boolean,
    color: Color,
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
        LineChart(
            data = listOf(line),
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
            labelProperties = LabelProperties(
                enabled = false
            ),
            minValue = diagram.min,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    }
}
