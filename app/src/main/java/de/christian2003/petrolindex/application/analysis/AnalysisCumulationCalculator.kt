package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint


/**
 * Calculates cumulated values.
 */
class AnalysisCumulationCalculator {

    /**
     * Calculates the cumulated values for the list of values that are passed as argument.
     *
     * @param dataPoints    List of values for which to calculate the cumulated values.
     * @return              Cumulated values.
     */
    fun calculateCumulateValuesFor(dataPoints: List<AnalysisDataPoint>): List<AnalysisDataPoint> {
        val cumulatedValues: MutableList<AnalysisDataPoint> = mutableListOf()
        var sum = 0

        dataPoints.forEach { dataPoint ->
            sum += dataPoint.value
            cumulatedValues.add(AnalysisDataPoint(dataPoint.date, sum))
        }

        return cumulatedValues
    }

}
