package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataLines
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary
import de.christian2003.petrolindex.application.repository.AnalysisRepository
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.analysis.AnalysisResult
import de.christian2003.petrolindex.domain.analysis.AnalysisResultMetadata
import de.christian2003.petrolindex.domain.analysis.AnalysisDiagram
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster
import de.christian2003.petrolindex.domain.analysis.AnalysisResultClusterType
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt


class ExtensiveAnalysisUseCase(
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
        val pricePerLiter: List<AnalysisDataPoint> = dataInterpolator.interpolate(dataLines.pricePerLiter, false)
        val interpolatedCumulatedVolumes: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedVolumes, false)
        val interpolatedCumulatedTotalPrices: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedTotalPrices, false)
        val interpolatedCumulatedDistancesTraveled: List<AnalysisDataPoint> = dataInterpolator.interpolate(cumulatedDistancesTraveled, false)

        //Generate analysis result:
        val analysisResult = AnalysisResult(
            volume = AnalysisResultCluster(
                sumDiagram = dataPointsToDiagram(interpolatedVolumes, 100.0),
                cumulatedDiagram = dataPointsToDiagram(interpolatedCumulatedVolumes, 100.0),
                totalSum = dataPointsToTotalSum(dataLines.volumes).toDouble() / 100.0,
                totalAverage = dataPointsToTotalAverage(dataLines.volumes, dataLines.volumesCount).toDouble() / 100.0,
                precisionAverage = dataPointsToPrecisionAverage(dataLines.volumes).toDouble() / 100.0,
                type = AnalysisResultClusterType.VOLUME
            ),
            totalPrice = AnalysisResultCluster(
                sumDiagram = dataPointsToDiagram(interpolatedTotalPrices, 100.0),
                cumulatedDiagram = dataPointsToDiagram(interpolatedCumulatedTotalPrices, 100.0),
                totalSum = dataPointsToTotalSum(dataLines.totalPrices).toDouble() / 100.0,
                totalAverage = dataPointsToTotalAverage(dataLines.totalPrices, dataLines.totalPricesCount).toDouble() / 100.0,
                precisionAverage = dataPointsToPrecisionAverage(dataLines.totalPrices).toDouble() / 100.0,
                type = AnalysisResultClusterType.TOTAL_PRICE
            ),
            distanceTraveled = AnalysisResultCluster(
                sumDiagram = dataPointsToDiagram(interpolatedDistancesTraveled, 1.0),
                cumulatedDiagram = dataPointsToDiagram(interpolatedCumulatedDistancesTraveled, 1.0),
                totalSum = dataPointsToTotalSum(dataLines.distancesTraveled).toDouble(),
                totalAverage = dataPointsToTotalAverage(dataLines.distancesTraveled, dataLines.distancesTraveledCount).toDouble(),
                precisionAverage = dataPointsToPrecisionAverage(dataLines.distancesTraveled).toDouble(),
                type = AnalysisResultClusterType.DISTANCE_TRAVELED
            ),
            pricePerLiterDiagram = dataPointsToDiagram(pricePerLiter, 100.0),
            metadata = AnalysisResultMetadata(
                start = start,
                end = end,
                createdAt = LocalDateTime.now(),
                analyzedConsumptionCount = consumptions.size,
                analysisTimeMillis = 1000,
                precision = precision
            )
        )

        return analysisResult
    }


    private fun dataPointsToDiagram(dataPoints: List<AnalysisDataPoint>, divisor: Double): AnalysisDiagram {
        val start: LocalDate = if (dataPoints.isEmpty()) {
            LocalDate.now()
        } else {
            dataPoints[0].date
        }

        val builder = AnalysisDiagram.Builder(start)

        dataPoints.forEach { dataPoint ->
            builder.addValue(dataPoint.value.toDouble() / divisor)
        }

        return builder.build()
    }


    /**
     * Calculates the average for the data points.
     *
     * @param dataPoints    List of data points.
     * @param count         Total count of consumptions to regard.
     */
    fun dataPointsToTotalAverage(dataPoints: List<AnalysisDataPoint>, count: Int): Int {
        if (count <= 0) {
            return 0
        }
        var sum = 0
        dataPoints.forEach { dataPoint ->
            sum += dataPoint.value
        }
        return sum / count
    }

    fun dataPointsToTotalSum(dataPoints: List<AnalysisDataPoint>): Int {
        var sum = 0
        dataPoints.forEach { dataPoint ->
            sum += dataPoint.value
        }
        return sum
    }

    fun dataPointsToPrecisionAverage(dataPoints: List<AnalysisDataPoint>): Int {
        if (dataPoints.isEmpty()) {
            return 0
        }
        var sum = 0
        dataPoints.forEach { dataPoint ->
            sum += dataPoint.value
        }
        return (sum.toDouble() / dataPoints.size).roundToInt()
    }

}
