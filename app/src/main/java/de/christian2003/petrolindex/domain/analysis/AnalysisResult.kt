package de.christian2003.petrolindex.domain.analysis


class AnalysisResult(

    val metadata: AnalysisResultMetadata,

    val volumeDiagram: Diagram,

    val totalPriceDiagram: Diagram,

    val distanceTraveledDiagram: Diagram,

    val cumulatedVolumeDiagram: Diagram,

    val cumulatedTotalPriceDiagram: Diagram,

    val cumulatedDistanceTraveledDiagram: Diagram

)
