package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.model.Consumption
import java.time.LocalDate


/**
 * Summarizes a list of consumptions based on an analysis precision.
 *
 * @param precision Precision for the analysis.
 */
class AnalysisDataSummarizer(
    private val precision: AnalysisPrecision
) {

    /**
     * Summarizes the passed list of consumptions based on the analysis precision. For each precision
     * (i.e. month, quarter or year), the resulting list contains one summary which summarizes the
     * consumptions for that precision.
     *
     * @param consumptions  List of consumptions to summarize based on the precision.
     * @return              List of summarized data for the precision.
     */
    fun summarize(consumptions: List<Consumption>): List<AnalysisDataSummary> {
        //Group the consumptions by precision:
        val grouped: Map<LocalDate, List<Consumption>> = consumptions.groupBy { consumption ->
            getPrecisionDateForConsumption(consumption.consumptionDate)
        }

        //Calculate sum for each map entry:
        val analysisDataSummaries: List<AnalysisDataSummary> = grouped.map { (date, consumption) ->
            createAnalysisDataSummary(date, consumption)
        }

        //Sort summaries by date:
        val sortedAnalysisDataSummaries: List<AnalysisDataSummary> = analysisDataSummaries.sortedBy { analysisDataSummary ->
            analysisDataSummary.date
        }

        return sortedAnalysisDataSummaries
    }


    /**
     * Based on the analysis precision, this converts the passed local date as follows:
     * - AnalysisPrecision.MONTH:   Date with the first day of the month is returned.
     * - AnalysisPrecision.QUARTER: Date with the first day of the quarter is returned.
     * - AnalysisPrecision.YEAR:    Date with the first day of the year is returned.
     *
     * @param consumptionDate   Date to convert.
     * @return                  Converted date.
     */
    private fun getPrecisionDateForConsumption(consumptionDate: LocalDate): LocalDate {
        when (precision) {
            AnalysisPrecision.MONTH -> {
                return consumptionDate.withDayOfMonth(1)
            }
            AnalysisPrecision.QUARTER -> {
                val quarterMonth = ((consumptionDate.month.value - 1) / 3) * 3 + 1 //Q1=1, Q2=4, Q3=7, Q4=10
                return consumptionDate.withDayOfMonth(1).withMonth(quarterMonth)
            }
            AnalysisPrecision.YEAR -> {
                return consumptionDate.withDayOfYear(1)
            }
        }
    }


    /**
     * Calculates an analysis data summary for each pair of date and consumptions.
     *
     * @param date          Date of the consumptions for which to create the summary.
     * @param consumptions  List of consumptions for which to create the summary.
     * @return              Summary of the data.
     */
    private fun createAnalysisDataSummary(date: LocalDate, consumptions: List<Consumption>): AnalysisDataSummary {
        var volumeSum = 0
        var totalPriceSum = 0
        var distanceTraveledSum = 0
        var volumeCount = 0
        var totalPriceCount = 0
        var distanceTraveledCount = 0
        consumptions.forEach { consumption ->
            volumeSum += consumption.volume
            volumeCount++
            totalPriceCount++
            totalPriceSum += consumption.totalPrice
            if (consumption.distanceTraveled != null) {
                distanceTraveledSum += consumption.distanceTraveled!!
                distanceTraveledCount++
            }
        }

        return AnalysisDataSummary(
            date = date,
            volumeSum = volumeSum,
            totalPriceSum = totalPriceSum,
            distanceTraveledSum = distanceTraveledSum,
            volumeCount = volumeCount,
            totalPriceCount = totalPriceCount,
            distanceTraveledCount = distanceTraveledCount
        )
    }

}
