package de.christian2003.petrolindex.domain.analysis


class AnalysisResult(
    val volume: AnalysisResultCluster,
    val totalPrice: AnalysisResultCluster,
    val distanceTraveled: AnalysisResultCluster,
    val pricePerLiterDiagram: AnalysisDiagram,
    val metadata: AnalysisResultMetadata,
)
