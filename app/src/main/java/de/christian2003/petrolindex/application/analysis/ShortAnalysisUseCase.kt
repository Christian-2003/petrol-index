package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.domain.analysis.ShortAnalysisResult
import de.christian2003.petrolindex.domain.model.Consumption


/**
 * Implements a short and simpler analysis that is called from the main screen.
 */
class ShortAnalysisUseCase {

    /**
     * Performs a short analysis of the data and returns the result.
     *
     * @param consumptions  List of consumptions to analyze
     * @return              Result of the analysis.
     */
    fun analyzeData(consumptions: List<Consumption>): ShortAnalysisResult {
        var pricePerLiterSum = 0
        var pricePerLiterCount = 0
        var volumeSum = 0
        var priceSum = 0
        var distanceTraveledSum = 0

        consumptions.forEach { consumption ->
            val pricePerLiter = consumption.calculatePricePerLiter()
            if (pricePerLiter > 0) {
                pricePerLiterSum += pricePerLiter
                pricePerLiterCount++
            }
            if (consumption.volume > 0) {
                volumeSum += consumption.volume
            }
            if (consumption.totalPrice > 0) {
                priceSum += consumption.totalPrice
            }
            if (consumption.distanceTraveled != null && consumption.distanceTraveled!! > 0) {
                distanceTraveledSum += consumption.distanceTraveled!!
            }
        }

        return ShortAnalysisResult(
            averagePricePerLiter = if (pricePerLiterCount > 0) { pricePerLiterSum / pricePerLiterCount } else { 0 },
            totalVolume = volumeSum,
            totalPrice = priceSum,
            totalDistanceTraveled = distanceTraveledSum
        )
    }

}
