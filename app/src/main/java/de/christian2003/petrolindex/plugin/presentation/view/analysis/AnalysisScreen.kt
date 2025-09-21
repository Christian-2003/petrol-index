package de.christian2003.petrolindex.plugin.presentation.view.analysis

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.analysis.AnalysisDiagram
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.Line


@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit
) {
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
                        .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                ) {
                    LineDiagram(
                        title = "Price per liter",
                        diagram = viewModel.analysisResult!!.pricePerLiterDiagram,
                        curvedEdges = true,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = {
                            viewModel.clusterToShow = viewModel.analysisResult!!.volume
                        }
                    ) {
                        Text("Volume")
                    }
                    Button(
                        onClick = {
                            viewModel.clusterToShow = viewModel.analysisResult!!.totalPrice
                        }
                    ) {
                        Text("Total price")
                    }
                    Button(
                        onClick = {
                            viewModel.clusterToShow = viewModel.analysisResult!!.distanceTraveled
                        }
                    ) {
                        Text("Distance traveled")
                    }
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
    }
}
