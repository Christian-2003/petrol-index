package de.christian2003.petrolindex.application.analysis.dto


data class AnalysisDataLines(
    val volumes: List<AnalysisDataPoint>,
    val totalPrices: List<AnalysisDataPoint>,
    val distancesTraveled: List<AnalysisDataPoint>
)
