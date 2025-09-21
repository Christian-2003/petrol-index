package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataLines
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary
import de.christian2003.petrolindex.application.repository.AnalysisRepository
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.analysis.AnalysisResult
import de.christian2003.petrolindex.domain.analysis.AnalysisResultMetadata
import de.christian2003.petrolindex.domain.analysis.Diagram
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime


class AnalysisUseCase(
    private val repository: AnalysisRepository
) {

    suspend fun analyzeData(start: LocalDate, end: LocalDate, precision: AnalysisPrecision): AnalysisResult {
        //Get all consumptions:
        val consumptions: List<Consumption> = repository.getConsumptionsForTimePeriod(start, end).first()

        //Summarize based on precision:
        val dataSummarizer = AnalysisDataSummarizer(precision)
        val summarizedData: List<AnalysisDataSummary> = dataSummarizer.summarize(consumptions)

        //Convert summaries to lines:
        val summariesToDataLines = AnalysisDataSummariesToDataLines()
        val dataLines: AnalysisDataLines = summariesToDataLines.convertToLines(summarizedData)

        //Calculate cumulated values:
        val cumulationCalculator = AnalysisCumulationCalculator()
        val cumulatedVolumes: List<AnalysisDataPoint> = cumulationCalculator.calculateCumulateValuesFor(dataLines.volumes)
        val cumulatedTotalPrices: List<AnalysisDataPoint> = cumulationCalculator.calculateCumulateValuesFor(dataLines.totalPrices)
        val cumulatedDistancesTraveled: List<AnalysisDataPoint> = cumulationCalculator.calculateCumulateValuesFor(dataLines.distancesTraveled)

        //Interpolate missing values:
        val dataInterpolator = AnalysisDataInterpolator(precision)
        val interpolatedVolumes: List<AnalysisDataPoint> = dataInterpolator.interpolate(dataLines.volumes, true)
        val interpolatedTotalPrices: List<AnalysisDataPoint> = dataInterpolator.interpolate(dataLines.totalPrices, true)
        val interpolatedDistancesTraveled: List<AnalysisDataPoint> = dataInterpolator.interpolate(dataLines.distancesTraveled, true)
        val interpolatedCumulatedVolumes: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedVolumes, true)
        val interpolatedCumulatedTotalPrices: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedTotalPrices, true)
        val interpolatedCumulatedDistancesTraveled: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedDistancesTraveled, true)

        //Generate analysis result:
        val analysisResult = AnalysisResult(
            volumeDiagram = dataPointsToDiagram(interpolatedVolumes, 100.0),
            totalPriceDiagram = dataPointsToDiagram(interpolatedTotalPrices, 100.0),
            distanceTraveledDiagram = dataPointsToDiagram(interpolatedDistancesTraveled, 1.0),
            cumulatedVolumeDiagram = dataPointsToDiagram(interpolatedCumulatedVolumes, 100.0),
            cumulatedTotalPriceDiagram = dataPointsToDiagram(interpolatedCumulatedTotalPrices, 100.0),
            cumulatedDistanceTraveledDiagram = dataPointsToDiagram(interpolatedCumulatedDistancesTraveled, 1.0),
            metadata = AnalysisResultMetadata(
                start = start,
                end = end,
                createdAt = LocalDateTime.now(),
                analyzedConsumptionCount = consumptions.size,
                analysisTimeMillis = 1000
            )
        )

        return analysisResult
    }



    private fun dataPointsToDiagram(dataPoints: List<AnalysisDataPoint>, divisor: Double): Diagram {
        val builder = Diagram.Builder()

        dataPoints.forEach { dataPoint ->
            builder.addValue(dataPoint.value.toDouble() / divisor)
        }

        return builder.build()
    }

}
