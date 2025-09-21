package de.christian2003.petrolindex.plugin.presentation.view.analysis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.application.services.CurrencyFormatterService
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster


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
                        .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                ) {
                    LineDiagram(
                        title = "Price per liter",
                        diagram = viewModel.analysisResult!!.pricePerLiterDiagram,
                        precision = viewModel.analysisPrecision,
                        curvedEdges = true,
                        color = MaterialTheme.colorScheme.error,
                        indicatorBuilder = {
                            currencyFormatter.format(it)
                        }
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
