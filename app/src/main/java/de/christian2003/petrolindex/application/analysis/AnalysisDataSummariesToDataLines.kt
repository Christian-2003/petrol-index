package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataLines
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary


/**
 * Converts a list of AnalysisDataSummary-instance into a single AnalysisDataLine-instance. This
 * AnalysisDataLine-instance contains lists of data points that can be used for consecutive analysis
 * steps.
 */
class AnalysisDataSummariesToDataLines {

    /**
     * Converts a list of AnalysisDataSummary-instances into a single AnalysisDataLine-instance.
     *
     * @param summaries List of summaries to convert.
     * @return          AnalysisDataLine-instance.
     */
    fun convertToLines(summaries: List<AnalysisDataSummary>): AnalysisDataLines {
        val volumes: MutableList<AnalysisDataPoint> = mutableListOf()
        val totalPrices: MutableList<AnalysisDataPoint> = mutableListOf()
        val distancesTraveled: MutableList<AnalysisDataPoint> = mutableListOf()

        summaries.forEach { summary ->
            if (summary.volumeCount > 0) {
                volumes.add(AnalysisDataPoint(summary.date, summary.volumeSum))
            }
            if (summary.totalPriceCount > 0) {
                totalPrices.add(AnalysisDataPoint(summary.date, summary.totalPriceSum))
            }
            if (summary.distanceTraveledCount > 0) {
                distancesTraveled.add(AnalysisDataPoint(summary.date, summary.distanceTraveledSum))
            }
        }

        return AnalysisDataLines(
            volumes = volumes,
            totalPrices = totalPrices,
            distancesTraveled = distancesTraveled
        )
    }

}
