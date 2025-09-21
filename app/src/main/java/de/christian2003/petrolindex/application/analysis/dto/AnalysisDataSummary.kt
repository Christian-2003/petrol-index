package de.christian2003.petrolindex.application.analysis.dto

import java.time.LocalDate

/**
 * Result for the analysis summary.
 *
 * @param date                  Date for which the consumption data is summarized.
 * @param volumeSum             Sum of the volume.
 * @param totalPriceSum         Sum of the total price.
 * @param distanceTraveledSum   Sum of the distance traveled.
 * @param volumeCount           Count of the volumes that were summarized.
 * @param totalPriceCount       Count of the total prices that were summarized.
 * @param distanceTraveledCount Count of the distance traveled that were summarized.
 */
data class AnalysisDataSummary(
    val date: LocalDate,
    val volumeSum: Int,
    val totalPriceSum: Int,
    val distanceTraveledSum: Int,
    val volumeCount: Int,
    val totalPriceCount: Int,
    val distanceTraveledCount: Int
)
