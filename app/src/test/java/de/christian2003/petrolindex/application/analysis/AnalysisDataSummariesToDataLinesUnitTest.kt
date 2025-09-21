package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisDataSummariesToDataLinesUnitTest {

    @Test
    fun testConversion() {
        val summaries: List<AnalysisDataSummary> = listOf(
            AnalysisDataSummary(
                date = LocalDate.of(2025, 1, 1),
                volumeSum = 1000,
                totalPriceSum = 3000,
                distanceTraveledSum = 350,
                volumeCount = 10,
                totalPriceCount = 10,
                distanceTraveledCount = 1
            ),
            AnalysisDataSummary(
                date = LocalDate.of(2025, 3, 1),
                volumeSum = 500,
                totalPriceSum = 1500,
                distanceTraveledSum = 0,
                volumeCount = 5,
                totalPriceCount = 5,
                distanceTraveledCount = 0
            ),
            AnalysisDataSummary(
                date = LocalDate.of(2025, 4, 1),
                volumeSum = 1000,
                totalPriceSum = 3000,
                distanceTraveledSum = 500,
                volumeCount = 10,
                totalPriceCount = 10,
                distanceTraveledCount = 1
            )
        )

        val converter = AnalysisDataSummariesToDataLines()

        val result = converter.convertToLines(summaries)

        //Test volumes:
        Assert.assertEquals(3, result.volumes.size)
        Assert.assertEquals(1000, result.volumes[0].value)
        Assert.assertEquals(500, result.volumes[1].value)
        Assert.assertEquals(1000, result.volumes[2].value)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.volumes[0].date)
        Assert.assertEquals(LocalDate.of(2025, 3, 1), result.volumes[1].date)
        Assert.assertEquals(LocalDate.of(2025, 4, 1), result.volumes[2].date)

        //Test total prices:
        Assert.assertEquals(3, result.totalPrices.size)
        Assert.assertEquals(3000, result.totalPrices[0].value)
        Assert.assertEquals(1500, result.totalPrices[1].value)
        Assert.assertEquals(3000, result.totalPrices[2].value)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.totalPrices[0].date)
        Assert.assertEquals(LocalDate.of(2025, 3, 1), result.totalPrices[1].date)
        Assert.assertEquals(LocalDate.of(2025, 4, 1), result.totalPrices[2].date)

        //Test distance traveled:
        Assert.assertEquals(2, result.distancesTraveled.size)
        Assert.assertEquals(350, result.distancesTraveled[0].value)
        Assert.assertEquals(500, result.distancesTraveled[1].value)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.distancesTraveled[0].date)
        Assert.assertEquals(LocalDate.of(2025, 4, 1), result.distancesTraveled[1].date)

    }

}
